package de.codebucket.mkkm.util;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TPayPayment {

    public static class Builder {

        private static final Map<String, String> TRANSLATIONS;

        static {
            Map<String, String> translationsMap = new HashMap<>();
            translationsMap.put("amount", "kwota");
            translationsMap.put("description", "opis");
            translationsMap.put("return_url", "pow_url");
            translationsMap.put("return_error_url", "pow_url_blad");
            translationsMap.put("name", "nazwisko");

            TRANSLATIONS = Collections.unmodifiableMap(translationsMap);
        }

        private final Map<String, String> params = new HashMap<>();
        private final String[] allowedFields = new String[] {
                "id", "amount", "description", "crc", "md5sum", "online", "return_url", "return_error_url", "email", "name"
        };

        /**
         * Default empty constructor.
         */
        public Builder() {

        }

        public Builder fromPaymentLink(String url) {
            final Uri data = Uri.parse(url);

            if (!data.getEncodedAuthority().equalsIgnoreCase("secure.transferuj.pl") && !data.getEncodedAuthority().equalsIgnoreCase("secure.tpay.com")) {
                throw new IllegalArgumentException("URL is not a TPay payment link");
            }

            if (data.getQueryParameter("id") == null) {
                throw new IllegalArgumentException("Merchant id cannot be null");
            }

            boolean useEnglishParams = true;

            // Figure out which parameter names are being used, english or polish
            if (data.getQueryParameter("kwota") != null) {
                useEnglishParams = false;
            }

            for (int i = 0; i < allowedFields.length; i++) {
                String field = allowedFields[i];
                if (!useEnglishParams && TRANSLATIONS.containsKey(field)) {
                    field = TRANSLATIONS.get(field);
                }

                if (!TextUtils.isEmpty(data.getQueryParameter(field))) {
                    params.put(allowedFields[i], data.getQueryParameter(field));
                }
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

            for (String field : allowedFields) {
                if (!TextUtils.isEmpty(params.get(field))) {
                    uriBuilder.appendQueryParameter(field, params.get(field));
                }
            }

            return uriBuilder.build();
        }
    }
}
