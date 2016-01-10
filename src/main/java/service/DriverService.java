package service;

import pojo.RealNameAuthInfo;
import pojo.TruckAuthInfo;
import pojo.TruckInfo;

/**
 * Created by I322233 on 1/4/2016.
 */
public interface DriverService {
    public boolean setRealNameInfo(int userId, RealNameAuthInfo realNameAuthInfo);
    public boolean setTruckAuthInfo(int userId, TruckAuthInfo truckAuthInfo);
    public boolean addTruck(int userId,TruckInfo truckInfo);
}
