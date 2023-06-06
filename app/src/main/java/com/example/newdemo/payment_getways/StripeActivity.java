package com.example.newdemo.payment_getways;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newdemo.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StripeActivity extends AppCompatActivity {
    
    private ImageView back_arrow_pay;
    private TextView price_tv1;
    private AppCompatButton pay_btn;
    private EditText card_number, expiryDate, et_ccv, firstName, lastname, et_country;
    private int cardMonth, cardYear, currentYear, currentMonth;
    private ProgressDialog progressDialog;
    private String vendorId = "", date = "", type = "", selectedTime = "", userId = "", description = "", ehr_id = "", userName = "", price = "", imagePath = "", paymentType = "", reportId = "", firstNameV = "", lastName = "", another = "";
    String currentDate = "", currentTime = "";
    private ArrayList<String> listPath = new ArrayList<>();
    private Spinner countrySpinner;
    private List<String> list = new ArrayList<>();
    String country = "";
    private String ehrDoc = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);


        findId();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void findId() {
        countrySpinner =findViewById(R.id.countrySpinner);

        et_country =findViewById(R.id.et_country);
        lastname =findViewById(R.id.lastname);
        firstName =findViewById(R.id.firstName);
        et_ccv =findViewById(R.id.et_ccv);
        expiryDate =findViewById(R.id.expiryDate);
        card_number =findViewById(R.id.card_number);
        pay_btn =findViewById(R.id.pay_btn);
        price_tv1 =findViewById(R.id.price_tv1);

        expiryDate.setOnClickListener(task -> {
            OpenDialog();
        });

        pay_btn.setOnClickListener(v -> {

            GetCardData();

        });
    }


    private void OpenDialog() {

        final Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        currentMonth = mMonth;
        currentYear = mYear;
        DatePickerDialog monthDatePickerDialog = new DatePickerDialog(this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                expiryDate.setText((month + 1) + "/" + year);
                cardMonth = month + 1;
                cardYear = year;
            }
        }, mYear, mMonth, mDay) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            }
        };
        monthDatePickerDialog.setTitle("select_month");
        monthDatePickerDialog.show();
    }

    private void GetCardData() {

        String cardNumver = card_number.getText().toString().trim();
        String fname = firstName.getText().toString().trim();
        String lname = lastname.getText().toString().trim();
        String ccvNumber = et_ccv.getText().toString().trim();
        // String country = et_country.getText().toString().trim();
        if (fname.isEmpty()) {
            Toast.makeText(StripeActivity.this, "enter first name", Toast.LENGTH_SHORT).show();
        } else if (cardNumver.isEmpty()) {
            Toast.makeText(StripeActivity.this, "enter card number", Toast.LENGTH_SHORT).show();
        } else if (lname.isEmpty()) {
            Toast.makeText(StripeActivity.this, "enter surname", Toast.LENGTH_SHORT).show();
        } else if (ccvNumber.isEmpty()) {
            Toast.makeText(StripeActivity.this, "enter CVV", Toast.LENGTH_SHORT).show();
        }  else if (currentYear <= cardYear) {
            if (currentYear == cardYear) {
                if (currentMonth > cardMonth) {
                    Toast.makeText(StripeActivity.this, "Enter valid exp month", Toast.LENGTH_SHORT).show();
                } else {
                    Verifycard(cardNumver, cardMonth, cardYear, ccvNumber);
                }
            } else {
                Verifycard(cardNumver, cardMonth, cardYear, ccvNumber);
            }
        } else {
            Toast.makeText(StripeActivity.this, "Enter valid exp Year", Toast.LENGTH_SHORT).show();
        }
    }

    public void Verifycard(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {

        progressDialog.show();

        Card card = new Card(
                cardNumber,
                cardExpMonth,
                cardExpYear,
                cardCVC
        );

        card.validateNumber();
        card.validateCVC();

        if (card != null) {
            Stripe stripe = new Stripe(StripeActivity.this, "pk_test_51JUrNKSD6L4j1iFvqJjAU3xzTqQiNpUoEUegAvfA683TO8AXtRZ7aXfp7kD3Xafcyl0sYzRsmueXEFvopyLwA47600Dc1q8oP0");
            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    progressDialog.dismiss();
                    Toast.makeText(StripeActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Token token) {
                    progressDialog.dismiss();
                    //  bookAppStripe(token.getId());
                    bookingTimeSlot(token.getId());
                    Log.i("token ", "---------" + token.getId());
                    // bookingAppointment(token.getId());
                    //  Toast.makeText(requireContext(), "done========", Toast.LENGTH_SHORT).show();
                    Log.d("onSuccess: ", token.getId());

                }
            });
        }
    }

    private void bookingTimeSlot(String id) {
        Toast.makeText(this, "payment Success "+id, Toast.LENGTH_SHORT).show();


    }
}