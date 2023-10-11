package com.bitbox.user.repository;

import com.bitbox.user.domain.Attendance;
import org.springframework.data.repository.CrudRepository;

public interface AttendanceRepository extends CrudRepository<Attendance, String> {

}
