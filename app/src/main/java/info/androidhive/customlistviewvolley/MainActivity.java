package info.androidhive.customlistviewvolley;

import info.androidhive.customlistviewvolley.adater.CustomListAdapter;
import info.androidhive.customlistviewvolley.app.AppController;
import info.androidhive.customlistviewvolley.model.Product;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

public class MainActivity extends Activity {

	// Log tag
	private static final String TAG = MainActivity.class.getSimpleName();

	// Products json url
	private static final String url = "http://www.shop13.gr/app_search.php";

	private ProgressDialog pDialog;
	private List<Product> productList = new ArrayList<Product>();
	//tha xrisimopoiithoun argotera
	/*private List<Product> caseList = new ArrayList<Product>();
	private List<Product> protectorList = new ArrayList<Product>();
	private List<Product> partsList = new ArrayList<Product>();
	private List<Product> chargeList = new ArrayList<Product>();*/
	//String caseType="144", protectorType="176", partsType="174", chargeType="180";
	private ListView listView;
	private CustomListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.list);
		//adapter gia ola ta proionta
		adapter = new CustomListAdapter(this, productList);
		//i lista mas tha exei ta periexomena tou adapter
		listView.setAdapter(adapter);

		pDialog = new ProgressDialog(this);
		// Showing progress dialog before making http request
		pDialog.setMessage("Loading...");
		pDialog.show();

		// changing action bar color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1b1b1b")));

		// Creating volley request obj
		JsonArrayRequest productReq = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						Log.d(TAG, response.toString());
						hidePDialog();

						// Parsing json
						for (int i = 0; i < response.length(); i++) {
							try {

								JSONObject obj = response.getJSONObject(i);
								Product product = new Product();
								product.setId(((Number) obj.get("id")).intValue());
								//greek encoding
                                try {
                                    product.setName(new String(obj.getString("name").getBytes("ISO-8859-7"), "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
								product.setThumbnailUrl(obj.getString("img"));
								product.setPrice(obj.getString("price"));
                                product.setSiteUrl(obj.getString("url"));
                                product.setType(obj.getString("type"));

								// adding product to products array
								productList.add(product);


							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						// notifying list adapter about data changes
						// so that it renders the list view with updated data
						adapter.notifyDataSetChanged();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						hidePDialog();

					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(productReq);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hidePDialog();
	}

	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
