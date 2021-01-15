package com.nikmaram.map.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class Repository {
    private static final String TAG = "Repositry";
    public static final Repository INSTACNCE;
        public final void CustomResponse( Single api,final Unit unit){
           SingleCom.com().add((Disposable)
                   api.subscribeOn(Schedulers.newThread())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribeWith(new DisposableSingleObserver() {
                       @Override
                       public void onSuccess(Object response) {
                           unit.invoke(response);
                       }

                       @Override
                       public void onError(Throwable e) {
                           Log.e(TAG, "onError: "+e.getMessage() );
                       }
                   }));
        }
        static {
            Repository resp = new Repository();
            INSTACNCE=resp;
        }
        public interface Unit{
            void invoke(Object object);
        }

        public static void Shared_write(Context context,String userid){
            SharedPreferences sharedPreferences=context.getSharedPreferences("token",0);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("userid",userid);
            editor.apply();
        }

        public static String Shared_Read(Context context){
            SharedPreferences sharedPreferences=context.getSharedPreferences("token",0);
            String userid=sharedPreferences.getString("userid",null);
            if(userid==null)
            return  null ;
            return userid;
        }



}
