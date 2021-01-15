package com.nikmaram.map.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nikmaram.map.Network.Api;
import com.nikmaram.map.Network.Repository;
import com.nikmaram.map.Network.SingleCom;
import com.nikmaram.map.model.RoutesModel;

public class ViewModel_Routes extends ViewModel {
   public MutableLiveData<RoutesModel> mutableRoutes =new MutableLiveData();
   public void Routes(String origin,String destination){
       Repository.INSTACNCE.CustomResponse(Api.Companion.invoke().Get_Routes(origin, destination), new Repository.Unit() {
           @Override
           public void invoke(Object object) {
               mutableRoutes.setValue((RoutesModel) object);
           }
       });
   }

    @Override
    protected void onCleared() {
        super.onCleared();
        SingleCom.com().clear();
    }
}