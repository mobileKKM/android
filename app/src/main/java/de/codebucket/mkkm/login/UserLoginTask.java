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
    private CallbackListener mListener;

    public UserLoginTask(@NonNull CallbackListener listener) {
        this(null, null, listener);
    }

    public UserLoginTask(@Nullable String username, @Nullable String password, @NonNull CallbackListener listener) {
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
        try {
            LoginHelper loginHelper = MobileKKM.getLoginHelper();
            int loginResult = username == null ? loginHelper.login() : loginHelper.login(username, password);

            // Don't proceed if login wasn't successful
            if (loginResult != Const.ErrorCode.SUCCESS) {
                mListener.onError(loginResult, null);
                cancel(true);
                return null;
            }

            // Execute post login callback<
            return mListener.onPostLogin();
        } catch (LoginFailedException ex) {
            // Error returned back by backend, contains error message
            mListener.onError(Const.ErrorCode.LOGIN_ERROR, ex.getMessage());
            cancel(true);
        } catch (IOException ex) {
            // Something went wrong with connection
            mListener.onError(Const.ErrorCode.CONNECTION_ERROR, null);
            cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            mListener.onSuccess(result);
        }

        mListener.onTaskFinish();
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        mListener.onTaskFinish();
        super.onCancelled();
    }

    public interface CallbackListener {
        Object onPostLogin() throws IOException;

        void onSuccess(Object result);

        void onError(int errorCode, String message);

        void onTaskFinish();
    }
}
