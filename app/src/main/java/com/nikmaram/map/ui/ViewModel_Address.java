package com.nikmaram.map.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nikmaram.map.Network.Api;
import com.nikmaram.map.Network.Repository;
import com.nikmaram.map.Network.SingleCom;
import com.nikmaram.map.model.AddressModel;

public class ViewModel_Address extends ViewModel {
    public MutableLiveData<AddressModel> mutableAddress =new MutableLiveData();
    public void Address(String lat,String lng){
        Repository.INSTACNCE.CustomResponse(Api.Companion.invoke().Get_Address(lat, lng), new Repository.Unit() {
            @Override
            public void invoke(Object object) {
                mutableAddress.setValue((AddressModel) object);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        SingleCom.com().clear();
    }
}
