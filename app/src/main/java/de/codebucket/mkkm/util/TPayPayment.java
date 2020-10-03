package de.codebucket.mkkm.util;

import android.net.Uri;
import android.text.TextUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class TPayPayment {

    public static class Builder {

        private final Map<String, String> params = new LinkedHashMap<>();

        public Builder() {

        }

        public Builder fromPaymentLink(String url) {
            Uri paymentUrl = Uri.parse(url);

            // Numeric identifier assigned to the merchant during the registration
            setId(paymentUrl.getQueryParameter("id"));

            // Transaction amount with dot as decimal separator
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("amount"))) {
                setAmount(paymentUrl.getQueryParameter("amount"));
            } else {
                setAmount(paymentUrl.getQueryParameter("kwota"));
            }

            // Transaction description
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("description"))) {
                setDescription(paymentUrl.getQueryParameter("description"));
            } else {
                setDescription(paymentUrl.getQueryParameter("opis"));
            }

            // Auxiliary parameter to identify the transaction on the merchant side
            setCrc(paymentUrl.getQueryParameter("crc"));

            // The checksum used to verify the parameters received from the merchant
            setMd5Sum(paymentUrl.getQueryParameter("md5sum"));

            // Allow online payments only – disallows selection of channels, which at this time, cannot process the payment in real time
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("online"))) {
                setOnline(paymentUrl.getQueryParameter("online"));
            }

            // The URL to which the client will be redirected after the correct transaction processing
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("return_url"))) {
                setReturnUrl(paymentUrl.getQueryParameter("return_url"));
            } else if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("pow_url"))) {
                setReturnUrl(paymentUrl.getQueryParameter("pow_url"));
            }

            // The URL to which the client will be redirected in case transaction error occurs
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("return_error_url"))) {
                setReturnErrorUrl(paymentUrl.getQueryParameter("return_error_url"));
            } else if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("pow_url_blad"))) {
                setReturnErrorUrl(paymentUrl.getQueryParameter("pow_url_blad"));
            }

            // Customer email
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("email"))) {
                setClientEmail(paymentUrl.getQueryParameter("email"));
            }

            // Customer name
            if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("name"))) {
                setClientName(paymentUrl.getQueryParameter("name"));
            } else if (!TextUtils.isEmpty(paymentUrl.getQueryParameter("nazwisko"))) {
                setClientName(paymentUrl.getQueryParameter("nazwisko"));
            }

            return this;
        }

        public String getId() {
            return params.get("id");
        }

        public Builder setId(String id) {
            params.put("id", id);
            return this;
        }

        public String getAmount() {
            return params.get("amount");
        }

        public Builder setAmount(String amount) {
            params.put("amount", amount);
            return this;
        }

        public String getDescription() {
            return params.get("description");
        }

        public Builder setDescription(String description) {
            params.put("description", description);
            return this;
        }

        public String getCrc() {
            return params.get("crc");
        }

        public Builder setCrc(String crc) {
            params.put("crc", crc);
            return this;
        }

        public String getMd5Sum() {
            return params.get("md5sum");
        }

        public Builder setMd5Sum(String md5sum) {
            params.put("md5sum", md5sum);
            return this;
        }

        public String getOnline() {
            return params.get("online");
        }

        public Builder setOnline(String online) {
            params.put("online", online);
            return this;
        }

        public String getReturnUrl() {
            return params.get("return_url");
        }

        public Builder setReturnUrl(String returnUrl) {
            params.put("return_url", returnUrl);
            return this;
        }

        public String getReturnErrorUrl() {
            return params.get("return_error_url");
        }

        public Builder setReturnErrorUrl(String returnErrorUrl) {
            params.put("return_error_url", returnErrorUrl);
            return this;
        }

        public String getClientEmail() {
            return params.get("email");
        }

        public Builder setClientEmail(String email) {
            params.put("email", email);
            return this;
        }

        public String getClientName() {
            return params.get("name");
        }

        public Builder setClientName(String name) {
            params.put("name", name);
            return this;
        }

        public Uri build() {
            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme("https")
                    .encodedAuthority("secure.tpay.com")
                    .encodedPath("/");

            for (Map.Entry<String, String> param : params.entrySet()) {
                if (param.getValue() != null) {
                    uriBuilder.appendQueryParameter(param.getKey(), param.getValue());
                }
            }

            return uriBuilder.build();
        }
    }
}
