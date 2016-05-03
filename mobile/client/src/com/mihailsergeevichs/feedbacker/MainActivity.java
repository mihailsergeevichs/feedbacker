/*
 * Copyright 2010-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.android.basicauth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;


public class MainActivity extends AbstractAsyncPushActivity {

	protected static final String TAG = MainActivity.class.getSimpleName();

	private EditText commentaryField;

    private RatingBar qualityBar;

    private RatingBar speedBar;

    private RatingBar priceBar;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);


        final Button sendButton = (Button) findViewById(R.id.submit);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PushFeedbackTask().execute();
            }
        });


	}

    private void displayResponse() {
        Toast.makeText(this, R.string.greetings, Toast.LENGTH_LONG).show();
    }

    private class PushFeedbackTask extends AsyncTask<Void, Void, Void> {

        private String quality;
        private String speed;
        private String price;
        private String commentary;
        private Calendar date = Calendar.getInstance();

        @Override
        protected void onPreExecute() {

            showLoadingProgressDialog();

            qualityBar = (RatingBar) findViewById(R.id.qualityBar);
            speedBar = (RatingBar) findViewById(R.id.speedBar);
            priceBar = (RatingBar) findViewById(R.id.priceBar);
            commentaryField = (EditText) findViewById(R.id.commentary);

            this.quality = String.valueOf(qualityBar.getRating());
            this.speed = String.valueOf(speedBar.getRating());
            this.price = String.valueOf(priceBar.getRating());
            this.commentary = commentaryField.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String url = "http://feedbacker.us-west-2.elasticbeanstalk.com/api/feedbacks";

            Feedback feedback = new Feedback();
            feedback.setId(null);
            feedback.setDate(null);
            feedback.setCommentary(commentary);
            feedback.setPrice(price);
            feedback.setQuality(quality);
            feedback.setSpeed(speed);

            HttpAuthentication authHeader = new HttpBasicAuthentication("admin", "admin");
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            try {
                restTemplate.postForObject(url, feedback, String.class);
            } catch (Throwable e){
                //do nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            dismissProgressDialog();
            commentaryField.setText("");
            qualityBar.setRating(5.0f);
            speedBar.setRating(5.0f);
            priceBar.setRating(5.0f);
            displayResponse();
        }
    }
}
