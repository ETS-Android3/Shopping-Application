package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    DataBaseHelper db;
    ArrayAdapter<String> a;
    String customer_id;
    ListView CartproductsList;
    ArrayList<String> productID = new ArrayList<String>();
    ArrayList<String> products = new ArrayList<String>();
    ArrayList<String> price = new ArrayList<String>();
    ArrayList<String> quantity = new ArrayList<String>();
    ArrayList<String> category = new ArrayList<String>();
    ArrayList<Integer> image = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = new DataBaseHelper(getApplicationContext());
        CustomerInfo c1 = (CustomerInfo)getIntent().getSerializableExtra("cust_object");
        customer_id = c1.getID();

        startActivity();

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNav);
        bottomNavigationView.setSelectedItemId(R.id.cart);
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
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        Button submitBtn = (Button)findViewById(R.id.proceedBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CartActivity.this, MapsActivity.class);
                i.putExtra("cust_id", customer_id);
                startActivity(i);
            }
        });
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
        public View getView(final int position, @Nullable View convertView, @Nullable ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.rowcart, parent, false);
            ImageView imageView = row.findViewById(R.id.image);
            final TextView name = row.findViewById(R.id.prodname);
            TextView cat = row.findViewById(R.id.catname);
            TextView price = row.findViewById(R.id.price);
            final TextView quan = row.findViewById(R.id.quantity);

            imageView.setImageResource(Imgs.get(position));
            name.setText(prod.get(position));
            cat.setText(categ.get(position));
            price.setText("Price: " + pr.get(position));
            quan.setText(q.get(position));

            final String order_id = db.getOrderID(customer_id);
            ImageButton addBtn = row.findViewById(R.id.addBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Integer quant = Integer.parseInt(q.get(position));
                    quant++;
                    db.updateQuantity(order_id, productID.get(position), String.valueOf(quant));
                    quan.setText(String.valueOf(quant));
                    q.set(position, String.valueOf(quant));
                    sumPrice();
                }
            });

            ImageButton remBtn = row.findViewById(R.id.remBtn);
            remBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer quant = Integer.parseInt(q.get(position));
                    quant--;
                    db.updateQuantity(order_id, productID.get(position), String.valueOf(quant));
                    quan.setText(String.valueOf(quant));
                    q.set(position, String.valueOf(quant));
                    sumPrice();
                }
            });

            ImageButton delBtn = row.findViewById(R.id.delBtn);
            String tmp = price.getText().toString();
            String []p = tmp.split(" ");
            final String finalPRice = p[1];

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pro_id = db.getProductForCart(name.getText().toString(), finalPRice);
                    db.deleteProduct(order_id, pro_id);
                    a.remove(getItem(position));
                    q.remove(position);
                    pr.remove(position);
                    sumPrice();
                }
            });

            return row;
        }
    }

    void startActivity()
    {
        String order_id = db.getOrderID(customer_id);
        if (order_id != null) {
            Cursor c = db.fetchCart(order_id);
            while (!c.isAfterLast()) {
                productID.add(c.getString(0));
                quantity.add(c.getString(1));

                Cursor cursor = db.fetchProductsInCart(c.getString(0));
                while (!cursor.isAfterLast()) {
                    products.add(cursor.getString(0));
                    price.add(cursor.getString(1));
                    image.add(R.drawable.ic_crop_original_black_24dp);

                    if (cursor.getString(2).equals("1"))
                        category.add("Skincare");
                    else if (cursor.getString(2).equals("2"))
                        category.add("Haircare");
                    else if (cursor.getString(2).equals("3"))
                        category.add("Makeup");
                    else
                        category.add("Perfume");
                    cursor.moveToNext();
                    break;
                }

                c.moveToNext();
            }

            CartproductsList = (ListView) findViewById(R.id.CartProductsList);
            final CartActivity.MyAdapter adapter = new CartActivity.MyAdapter(this, products, price, quantity, category, image);
            a = adapter;
            CartproductsList.setAdapter(adapter);

            sumPrice();
        }
    }

    void sumPrice()
    {
        //Sum
        TextView priceText = (TextView)findViewById(R.id.priceText);
        Integer sum = 0;
        for(int i = 0; i < quantity.size(); i++)
        {
            sum += (Integer.parseInt(quantity.get(i)) * Integer.parseInt(price.get(i)));
        }
        priceText.setText(String.valueOf(sum));
    }
}
