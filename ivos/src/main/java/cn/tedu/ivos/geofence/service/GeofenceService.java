package cn.tedu.ivos.geofence.service;

import cn.tedu.ivos.geofence.pojo.dto.GeofenceQuery;
import cn.tedu.ivos.geofence.pojo.dto.GeofenceSaveParam;
import cn.tedu.ivos.geofence.pojo.vo.GeofenceVO;

import java.util.List;

public interface GeofenceService {
    List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery);

    void updateStatus(Long id, String status);

    void deleteGeofence(Long id);

    void saveGeofence(GeofenceSaveParam geofenceSaveParam);
}
