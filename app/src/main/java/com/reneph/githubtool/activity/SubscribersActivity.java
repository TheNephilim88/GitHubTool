package com.reneph.githubtool.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reneph.githubtool.R;
import com.reneph.githubtool.adapter.SubscriberAdapter;
import com.reneph.githubtool.data.RepositoryData;
import com.reneph.githubtool.util.GitHubClient;
import com.reneph.githubtool.util.JSONUtil;
import com.reneph.githubtool.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubscribersActivity extends AppCompatActivity {
    private RecyclerView listSubscribers;
    private TextView mEmptyView;
    private TextView mHeaderRepository;
    private TextView mHeaderSubscribers;
    private RepositoryData mRepositoryData;
    private ProgressDialog progressDialog;
    private SubscriberAdapter mSubscriberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        listSubscribers = (RecyclerView) findViewById(R.id.list_subscribers);
        listSubscribers.setLayoutManager(new LinearLayoutManager(this));
        listSubscribers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mHeaderRepository = (TextView) findViewById(R.id.header_repository);
        mHeaderSubscribers = (TextView) findViewById(R.id.header_subscribers);

        mEmptyView = (TextView) findViewById(android.R.id.empty);
        updateEmptyView();

        Bundle intentExtras = getIntent().getExtras();
        if((intentExtras != null) && (intentExtras.getInt("repository_id", -1) > -1)){
            loadRepositoryData(intentExtras.getInt("repository_id", -1));
        }else{
            finish();
        }
    }

    private void loadRepositoryData(int repositoryId){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // show error message and go back to main window if no internet connection is available
        if (!((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()))) {
            Toast.makeText(this, R.string.error_no_internet_connection, Toast.LENGTH_LONG).show();
            finish();
        } else { // start fetching & parsing if internet connection is available
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loading_indicator));
            progressDialog.show();

            JsonObjectRequest repositoriesRequest = new JsonObjectRequest(Request.Method.GET,
                    JSONUtil.buildRepositoryQueryURI(repositoryId),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mRepositoryData = new RepositoryData(response);
                            mHeaderRepository.setText(mRepositoryData.getRepositoryName());
                            mHeaderSubscribers.setText(String.format(getString(R.string.subscriber_count), mRepositoryData.getSubscribers()));
                            loadSubscribersData();
                        }
                    }, mErrorListener);

            GitHubClient.getInstance(this).addToRequestQueue(repositoriesRequest);
        }
    }

    private void loadSubscribersData(){
        JsonArrayRequest forksRequest = new JsonArrayRequest(Request.Method.GET,
                mRepositoryData.getSubscribersURL(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null) {
                            mRepositoryData.parseSubscribersList(response);

                            mSubscriberAdapter = new SubscriberAdapter(mRepositoryData.getSubscriberList(), getBaseContext());
                            listSubscribers.setAdapter(mSubscriberAdapter);
                            updateEmptyView();
                        }

                        progressDialog.dismiss();
                    }
                }, mErrorListener);

        GitHubClient.getInstance(getBaseContext()).addToRequestQueue(forksRequest);
    }

    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                // usually, if there are any client errors, GitHub will send response with a message about what went wrong
                // see https://developer.github.com/v3/#client-errors
                if ((error != null) && (error.networkResponse != null) && (error.networkResponse.data != null)) {
                    // check if both response and message are supplied
                    JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                    Toast.makeText(getApplicationContext(), String.valueOf(error.networkResponse.statusCode) + ": " + errorResponse.getString("message"), Toast.LENGTH_LONG).show();
                } else { // otherwise show generic error message
                    Toast.makeText(getApplicationContext(), R.string.error_fetching_data, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // show generic error message if network response could not get parsed as json-object
                Toast.makeText(getApplicationContext(), R.string.error_fetching_data, Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
        }
    };

    private void updateEmptyView(){
        // show or hide empty-view regarding amount of items that are actually in the adapter
        if((mSubscriberAdapter == null) || (mSubscriberAdapter.getItemCount() == 0)){
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}