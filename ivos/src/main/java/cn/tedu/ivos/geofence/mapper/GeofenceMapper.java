package cn.tedu.ivos.geofence.mapper;

import cn.tedu.ivos.geofence.pojo.dto.GeofenceQuery;
import cn.tedu.ivos.geofence.pojo.entity.Geofence;
import cn.tedu.ivos.geofence.pojo.vo.GeofenceVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceMapper {
    List<GeofenceVO> selectGeofence(GeofenceQuery geofenceQuery);

    void update(Geofence geofence);

    void deleteById(Long id);

    void insert(Geofence geofence);
}
