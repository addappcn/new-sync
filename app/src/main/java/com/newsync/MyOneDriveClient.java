package com.newsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.newsync.activity.TranslucentActivity;
import com.onedrive.sdk.authentication.ClientAuthenticatorException;
import com.onedrive.sdk.authentication.IAccountInfo;
import com.onedrive.sdk.authentication.IAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IExecutors;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.core.OneDriveErrorCodes;
import com.onedrive.sdk.extensions.DriveRequestBuilder;
import com.onedrive.sdk.extensions.IDriveRequestBuilder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.generated.BaseOneDriveClient;
import com.onedrive.sdk.http.IHttpProvider;
import com.onedrive.sdk.logger.ILogger;
import com.onedrive.sdk.serializer.ISerializer;

/**
 * Created by qgswsg on 2018/3/2.
 */

public class MyOneDriveClient extends BaseOneDriveClient implements IOneDriveClient {
    /**
     * Restricted constructor
     */
    protected MyOneDriveClient() {
    }

    private String emailAddressHint;

    /**
     * Gets a request builder for the default drive
     *
     * @return The request builder
     */
    @Override
    public IDriveRequestBuilder getDrive() {
        return new DriveRequestBuilder(getServiceRoot() + "/drive", this, null);
    }

    /**
     * The builder for this OneDriveClient
     */
    public static class Builder {

        /**
         * The client under construction
         */
        private final MyOneDriveClient mClient = new MyOneDriveClient();

        /**
         * Sets the serializer
         *
         * @param serializer The serializer
         * @return the instance of this builder
         */
        public MyOneDriveClient.Builder serializer(final ISerializer serializer) {
            mClient.setSerializer(serializer);
            return this;
        }

        /**
         * Sets the httpProvider
         *
         * @param httpProvider The httpProvider
         * @return the instance of this builder
         */
        public MyOneDriveClient.Builder httpProvider(final IHttpProvider httpProvider) {
            mClient.setHttpProvider(httpProvider);
            return this;
        }

        /**
         * Sets the authenticator
         *
         * @param authenticator The authenticator
         * @return the instance of this builder
         */
        public MyOneDriveClient.Builder authenticator(final IAuthenticator authenticator) {
            mClient.setAuthenticator(authenticator);
            return this;
        }

        /**
         * Sets the executors
         *
         * @param executors The executors
         * @return the instance of this builder
         */
        public MyOneDriveClient.Builder executors(final IExecutors executors) {
            mClient.setExecutors(executors);
            return this;
        }

        /**
         * Sets the logger
         *
         * @param logger The logger
         * @return the instance of this builder
         */
        private MyOneDriveClient.Builder logger(final ILogger logger) {
            mClient.setLogger(logger);
            return this;
        }

        /**
         * Set this builder based on the client configuration
         *
         * @param clientConfig The client configuration
         * @return the instance of this builder
         */
        public MyOneDriveClient.Builder fromConfig(final IClientConfig clientConfig) {
            return this.authenticator(clientConfig.getAuthenticator())
                    .executors(clientConfig.getExecutors())
                    .httpProvider(clientConfig.getHttpProvider())
                    .logger(clientConfig.getLogger())
                    .serializer(clientConfig.getSerializer());
        }

        /**
         * Login a user and then returns the OneDriveClient asynchronously
         *
         * @param activity The activity the UI should be from
         * @param callback The callback when the client has been built
         */
        public void loginAndBuildClient(final Activity activity, final ICallback<IOneDriveClient> callback) {
            mClient.validate();

            mClient.getExecutors().performOnBackground(new Runnable() {
                @Override
                public void run() {
                    final IExecutors executors = mClient.getExecutors();
                    try {
                        executors.performOnForeground(loginAndBuildClient(activity), callback);
                    } catch (final ClientException e) {
                        executors.performOnForeground(e, callback);
                    }
                }
            });
        }

        /**
         * Login a user and then returns the OneDriveClient
         *
         * @param activity The activity the UI should be from
         * @throws ClientException if there was an exception creating the client
         */
        private IOneDriveClient loginAndBuildClient(final Activity activity) throws ClientException {
            mClient.validate();

            mClient.getAuthenticator()
                    .init(mClient.getExecutors(), mClient.getHttpProvider(), activity, mClient.getLogger());

            IAccountInfo silentAccountInfo = null;
            try {
                silentAccountInfo = mClient.getAuthenticator().loginSilent();
            } catch (final Exception ignored) {
            }

            if (silentAccountInfo == null) {
                if (activity instanceof TranslucentActivity) {
                    activity.runOnUiThread(()->{
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("新同步")
                                .setMessage("静默登录遇到问题，登录凭证已过期或用户密码已在其它终端修改，请重新登录!")
                                .setNegativeButton("重新登录", (dialogInterface, i) -> {
                                    mClient.emailAddressHint = activity.getSharedPreferences("MSAAuthenticatorPrefs", Context.MODE_PRIVATE)
                                            .getString("userId", null);
                                    mClient.getExecutors().performOnBackground(()->{
                                        if (mClient.getAuthenticator().login(mClient.emailAddressHint) == null) {
                                            throw new ClientAuthenticatorException("Unable to authenticate silently or interactively",
                                                    OneDriveErrorCodes.AuthenticationFailure);
                                        }
                                    });
                                }).show();
                    });
                } else if (mClient.getAuthenticator().login(mClient.emailAddressHint) == null) {
                    throw new ClientAuthenticatorException("Unable to authenticate silently or interactively",
                            OneDriveErrorCodes.AuthenticationFailure);
                }
            }

            return mClient;
        }
    }
}
