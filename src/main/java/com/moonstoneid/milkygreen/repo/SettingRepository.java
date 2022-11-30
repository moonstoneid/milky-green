package com.moonstoneid.milkygreen.repo;

import com.moonstoneid.milkygreen.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

}
