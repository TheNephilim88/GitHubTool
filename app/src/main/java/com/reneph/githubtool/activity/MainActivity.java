package com.reneph.githubtool.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.reneph.githubtool.R;
import com.reneph.githubtool.adapter.RepositoryAdapter;
import com.reneph.githubtool.data.RepositoryData;
import com.reneph.githubtool.util.GitHubClient;
import com.reneph.githubtool.util.JSONUtil;
import com.reneph.githubtool.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mSearchField;
    private TextView mEmptyView;
    private List<RepositoryData> mRepositoryListing;
    private RepositoryAdapter mRepositoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchField = (EditText) findViewById(R.id.search_field);

        RecyclerView listRepositories = (RecyclerView) findViewById(R.id.list_repositories);
        listRepositories.setLayoutManager(new LinearLayoutManager(this));
        listRepositories.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mEmptyView = (TextView) findViewById(android.R.id.empty);

        ImageButton mSearchRepositories = (ImageButton) findViewById(R.id.search_repositories);
        mSearchRepositories.setOnClickListener(this);

        mRepositoryListing = new ArrayList<>();
        mRepositoryAdapter = new RepositoryAdapter(mRepositoryListing, this);

        listRepositories.setAdapter(mRepositoryAdapter);
        updateEmptyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_repositories:
                mRepositoryListing.clear();

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

                if (!((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()))) { // show error message if no internet connection is available
                    Toast.makeText(this, R.string.error_no_internet_connection, Toast.LENGTH_LONG).show();
                } else { // start fetching & parsing if internet connection is available
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.loading_indicator));
                    progressDialog.show();

                    JsonObjectRequest repositoriesRequest = new JsonObjectRequest(Request.Method.GET,
                            JSONUtil.buildQueryURI(mSearchField.getText().toString()),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray jsonResultItems;
                                    try {
                                        jsonResultItems = response.getJSONArray("items");

                                        if (jsonResultItems != null) {
                                            for (int i = 0; i < jsonResultItems.length(); i++) {
                                                RepositoryData entry = JSONUtil.parseJSONToRepository(jsonResultItems.getJSONObject(i));

                                                if (entry != null) {
                                                    mRepositoryListing.add(entry);
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), R.string.error_parsing_json, Toast.LENGTH_LONG).show();
                                    }

                                    mRepositoryAdapter.notifyDataSetChanged();
                                    updateEmptyView();
                                    progressDialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                // usually, if there are any client errors, github will send response with a message about what went wrong
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

                            mRepositoryAdapter.notifyDataSetChanged();
                            updateEmptyView();
                            progressDialog.dismiss();
                        }
                    });

                    GitHubClient.getInstance(this).addToRequestQueue(repositoriesRequest);
                }
        }
    }

    private void updateEmptyView(){
        // show or hide empty-view regarding amount of items that are actually in the adapter
        if(mRepositoryAdapter.getItemCount() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
