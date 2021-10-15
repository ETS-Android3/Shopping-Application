package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    String customer_id;
    ListView allproductsList;
    ArrayList<String> products = new ArrayList<String>();
    ArrayList<String> price = new ArrayList<String>();
    ArrayList<String> quantity = new ArrayList<String>();
    ArrayList<String> category = new ArrayList<String>();
    ArrayList<Integer> image = new ArrayList<Integer>();
    ArrayList<String> barCocdes = new ArrayList<String>();
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barCocdes.add("9502930974723");
        barCocdes.add("647923330649");
        barCocdes.add("8718114615165");
        barCocdes.add("773602069989");
        barCocdes.add("773602103492");
        barCocdes.add("3349668543083");
        barCocdes.add("5900717141810");
        barCocdes.add("3605532978666");
        barCocdes.add("709102928713");
        barCocdes.add("086800860334");

        db = new DataBaseHelper(getApplicationContext());

        CustomerInfo c1 = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
        customer_id = c1.getID();
        startActivity();

        //Search by Text
        SearchView searchView = (SearchView)findViewById(R.id.searchtxt);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                products.clear();
                price.clear();
                category.clear();
                quantity.clear();

                Cursor cursor = db.searchProducts(s);

                while(!cursor.isAfterLast())
                {
                    products.add(cursor.getString(1));
                    price.add(cursor.getString(2));
                    quantity.add(cursor.getString(3));
                    if(cursor.getString(4).equals("1"))
                        category.add("Skincare");
                    else if(cursor.getString(4).equals("2"))
                        category.add("Haircare");
                    else if(cursor.getString(4).equals("3"))
                        category.add("Makeup");
                    else
                        category.add("Perfume");
                    cursor.moveToNext();
                }

                allproductsList = (ListView)findViewById(R.id.AllProductsList);
                final MyAdapter adapter = new MyAdapter(getApplication(), products, price, quantity, category, image);
                allproductsList.setAdapter(adapter);

                return false;
            }
        });

        //Search by voice
        ImageButton voiceBtn = (ImageButton)findViewById(R.id.VoiceBtn);
        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

        //search by camera
        ImageButton camBtn = (ImageButton)findViewById(R.id.CamBtn);
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaneCode();
            }
        });

        //BottomNav
        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNav);
        bottomNavigationView.setSelectedItemId(R.id.homepage);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                CustomerInfo c;
                Intent i;
                switch (item.getItemId())
                {
                    case R.id.account:
                        c = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
                        customer_id = c.getID();
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cart:
                        c = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
                        customer_id = c.getID();
                        i = new Intent(getApplicationContext(), CartActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cat:
                        c = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
                        customer_id = c.getID();
                        i = new Intent(getApplicationContext(), CategoriesActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.homepage:
                        c = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
                        customer_id = c.getID();
                        startActivity();
                        return true;
                }
                return false;
            }
        });
    }

    private void speak()
    {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "What are you lokking for?");

        try
        {
            startActivityForResult(i, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && null != data)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchByVoice(result.get(0));
            }

        }
        else{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null && resultCode == RESULT_OK) {
                if (result.getContents() != null) {
                    searchByImage(result.getContents());
                }
            }
        }
    }

    void searchByVoice(String s)
    {
        products.clear();
        price.clear();
        category.clear();
        quantity.clear();

        Cursor cursor = db.searchProducts(s);

        while(!cursor.isAfterLast())
        {
            products.add(cursor.getString(1));
            price.add(cursor.getString(2));
            quantity.add(cursor.getString(3));
            if(cursor.getString(4).equals("1"))
                category.add("Skincare");
            else if(cursor.getString(4).equals("2"))
                category.add("Haircare");
            else if(cursor.getString(4).equals("3"))
                category.add("Makeup");
            else
                category.add("Perfume");
            cursor.moveToNext();
        }

        allproductsList = (ListView)findViewById(R.id.AllProductsList);
        final MyAdapter adapter = new MyAdapter(getApplication(), products, price, quantity, category, image);
        allproductsList.setAdapter(adapter);
    }

    void ScaneCode()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    void searchByImage(String c)
    {
        if(barCocdes.contains(c)) {
            Integer index = barCocdes.indexOf(c);
            String s = products.get(index);

            products.clear();
            price.clear();
            category.clear();
            quantity.clear();

            Cursor cursor = db.searchProducts(s);

            while (!cursor.isAfterLast()) {
                products.add(cursor.getString(1));
                price.add(cursor.getString(2));
                quantity.add(cursor.getString(3));
                if (cursor.getString(4).equals("1"))
                    category.add("Skincare");
                else if (cursor.getString(4).equals("2"))
                    category.add("Haircare");
                else if (cursor.getString(4).equals("3"))
                    category.add("Makeup");
                else
                    category.add("Perfume");
                cursor.moveToNext();
            }
        }
        allproductsList = (ListView)findViewById(R.id.AllProductsList);
        final MyAdapter adapter = new MyAdapter(getApplication(), products, price, quantity, category, image);
        allproductsList.setAdapter(adapter);
    }

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        ArrayList<String> prod;
        ArrayList<String> pr;
        ArrayList<String> q;
        ArrayList<String> categ;
        ArrayList<Integer> Imgs;

        MyAdapter(Context c, ArrayList<String> p, ArrayList<String> o, ArrayList<String> qu, ArrayList<String> ca, ArrayList<Integer> im)
        {
            super(c, R.layout.row, R.id.prodname, p);

            this.context = c;
            this.prod = p;
            this.pr = o;
            this.q = qu;
            this.categ = ca;
            this.Imgs = im;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView imageView = row.findViewById(R.id.image);
            final TextView name = row.findViewById(R.id.prodname);
            TextView cat = row.findViewById(R.id.catname);
            final TextView price = row.findViewById(R.id.price);
            final TextView quan = row.findViewById(R.id.quantity);

            imageView.setImageResource(Imgs.get(position));
            name.setText(prod.get(position));
            cat.setText(categ.get(position));
            price.setText("Price: " + pr.get(position));
            quan.setText("Quantity: " + q.get(position));

            final Integer pos = position;

            final Button addTocart = row.findViewById(R.id.Add);
            addTocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(db.OrderExist(customer_id))
                    {
                        String Order_id = db.getOrderID(customer_id);
                        String pro_id = db.getProductID(name.getText().toString(), pr.get(pos), q.get(pos));
                        db.addOrderDetails(Order_id, pro_id);
                    }
                    else
                    {
                        db.addOrder(customer_id);
                        String Order_id = db.getOrderID(customer_id);
                        String pro_id = db.getProductID(name.getText().toString(), pr.get(pos), q.get(pos));
                        db.addOrderDetails(Order_id, pro_id);
                    }
                    Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG).show();
                    addTocart.setClickable(false);
                }
            });

            return row;
        }
    }

    void startActivity()
    {
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);
        image.add(R.drawable.ic_crop_original_black_24dp);

        Cursor cursor = db.fetchAllProducts();
        products.clear();
        price.clear();
        category.clear();
        quantity.clear();

        while(!cursor.isAfterLast())
        {
            products.add(cursor.getString(1));
            price.add(cursor.getString(2));
            quantity.add(cursor.getString(3));
            if(cursor.getString(4).equals("1"))
                category.add("Skincare");
            else if(cursor.getString(4).equals("2"))
                category.add("Haircare");
            else if(cursor.getString(4).equals("3"))
                category.add("Makeup");
            else
                category.add("Perfume");
            cursor.moveToNext();
        }

        allproductsList = (ListView)findViewById(R.id.AllProductsList);
        final MyAdapter adapter = new MyAdapter(this, products, price, quantity, category, image);
        allproductsList.setAdapter(adapter);
    }
}
