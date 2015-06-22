package com.arcgis.uploadofflinedata;

/**网络状态改变之后，通过此接口的实例通知当前网络的状态，此接口在App中注入实例对象
 * Created by pang congcong on 2015/6/10.
 */
public interface IConnectState
{
    void GetState(boolean isConnected);
}
