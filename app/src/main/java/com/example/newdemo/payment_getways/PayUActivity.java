package com.example.newdemo.payment_getways;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newdemo.HashGenerationUtils;
import com.example.newdemo.R;
import com.example.newdemo.databinding.ActivityPayUactivityBinding;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;


import java.util.HashMap;

public class PayUActivity extends AppCompatActivity {
    ActivityPayUactivityBinding binding;


    private static final String SUCCESS_URL = "https://payu.herokuapp.com/success";
    private static final String FAILURE_URL = "https://payu.herokuapp.com/failure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayUactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.payubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentSetup();
            }
        });
    }

    private void paymentSetup() {
        HashMap<String, Object> additionalParamsMap = new HashMap<>();
        additionalParamsMap.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParamsMap.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParamsMap.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParamsMap.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParamsMap.put(PayUCheckoutProConstants.CP_UDF5, "udf5");
        additionalParamsMap.put(PayUCheckoutProConstants.SODEXO_SOURCE_ID, "srcid123");

        PayUPaymentParams payUPaymentParams = new PayUPaymentParams.Builder()
                .setAmount("1.0")
                .setIsProduction(true)
                .setKey("smsplus")
                .setProductInfo("Macbook Pro")
                .setPhone("8888888888")
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setFirstName("John")
                .setEmail("john@yopmail.com")
                .setSurl(SUCCESS_URL)
                .setFurl(FAILURE_URL)
                .setAdditionalParams(additionalParamsMap)
                .build();

        PayUCheckoutPro.open(PayUActivity.this, payUPaymentParams, new PayUCheckoutProListener() {
            @Override
            public void onPaymentSuccess(@NonNull Object o) {

            }

            @Override
            public void onPaymentFailure(@NonNull Object o) {

            }


            @Override
            public void onPaymentCancel(boolean isTxnInitiated) {
                // Handle payment cancellation
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    // Handle error
                }
            }

            @Override
            public void setWebViewProperties(WebView webView, Object bank) {
                // Set webview properties, if needed
            }

            @Override
            public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);

                if (hashData != null && !hashData.isEmpty() && hashName != null && !hashName.isEmpty()) {
                    // Generate hash from your server-side and return it
                    String hash = HashGenerationUtils.generateHashFromSDK(hashData, "smsplus", "smsplus");
                    if (hash != null && !hash.isEmpty()) {
                        HashMap<String, String> dataMap = new HashMap<>();
                        dataMap.put(hashName, hash);
                        hashGenerationListener.onHashGenerated(dataMap);
                    }
                }
            }
        });
    }
}
