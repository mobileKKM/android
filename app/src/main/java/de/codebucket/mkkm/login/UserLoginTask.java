package de.codebucket.mkkm.login;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.util.Const;

public class UserLoginTask extends AsyncTask<Void, Void, Object> {

    @Nullable
    public final String username, password;

    @NonNull
    private OnCallbackListener mListener;

    public UserLoginTask(@NonNull OnCallbackListener listener) {
        this(null, null, listener);
    }

    public UserLoginTask(@Nullable String username, @Nullable String password, @NonNull OnCallbackListener listener) {
        this.username = username;
        this.password = password;

        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        // Check if device is connected to the Internet
        if (!MobileKKM.getInstance().isNetworkConnectivity()) {
            mListener.onError(Const.ErrorCode.NO_NETWORK, null);
            cancel(true);
            return;
        }

        // Don't continue if fingerprint is invalid
        if (!MobileKKM.getLoginHelper().isFingerprintValid()) {
            mListener.onError(Const.ErrorCode.INVALID_FINGERPRINT, null);
            cancel(true);
            return;
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {
        ErrorResult error = null;

        try {
            LoginHelper loginHelper = MobileKKM.getLoginHelper();
            int loginResult = username == null ? loginHelper.login() : loginHelper.login(username, password);

            // Execute post login callback if login was successful
            if (loginResult == Const.ErrorCode.SUCCESS) {
                return mListener.onPostLogin();
            }

            error = new ErrorResult(loginResult);
        } catch (LoginFailedException ex) {
            // Error returned back by backend, contains error message
            error = new ErrorResult(Const.ErrorCode.LOGIN_ERROR, ex.getMessage());
        } catch (IOException ex) {
            // Something went wrong with connection
            error = new ErrorResult(Const.ErrorCode.CONNECTION_ERROR);
        }

        cancel(false);
        return error;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            mListener.onSuccess(result);
        }

        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled(Object result) {
        if (result instanceof ErrorResult) {
            ErrorResult errorResult = (ErrorResult) result;
            mListener.onError(errorResult.code, errorResult.message);
        }

        super.onCancelled(result);
    }

    private class ErrorResult {

        public int code;
        public String message;

        ErrorResult(int errorCode) {
            this(errorCode, Const.getErrorMessage(errorCode, null));
        }

        ErrorResult(int errorCode, String errorMessage) {
            code = errorCode;
            message = errorMessage;
        }
    }

    public interface OnCallbackListener {
        Object onPostLogin() throws IOException;

        void onSuccess(Object result);

        void onError(int errorCode, String message);
    }
}
