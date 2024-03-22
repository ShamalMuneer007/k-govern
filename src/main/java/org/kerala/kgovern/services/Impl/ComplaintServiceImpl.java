package org.kerala.kgovern.services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.ComplaintStatus;
import org.kerala.kgovern.entities.DepartmentComplaint;
import org.kerala.kgovern.entities.DepartmentNews;
import org.kerala.kgovern.repositories.DepartmentComplaintRepository;
import org.kerala.kgovern.repositories.NewsRepository;
import org.kerala.kgovern.services.ComplaintService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class ComplaintServiceImpl implements ComplaintService {
    private final DepartmentComplaintRepository complaintRepository;
    private final NewsRepository newsRepository;
    @Scheduled(cron = "*/30 * * * * *")
    public void checkComplaintNoResponseTime(){
        List<DepartmentComplaint> complaintList = complaintRepository.findAll();
        List<DepartmentNews> newsList = newsRepository.findAll();
        log.info("checking complaint response time !!!");
        complaintList
                .forEach(complaint ->{
                    log.info(complaint.getStatus().name());
                    if(complaint.getIssuedDateTime().plusMinutes(1).isBefore(LocalDateTime.now())
                            &&complaint.getStatus().name().equals("PENDING")){
                        log.info("Changing complaint status");
                        complaint.setStatus(ComplaintStatus.NO_RESPONSE);
                        complaintRepository.save(complaint);
                    }
                });
        newsList.forEach(news -> {
            if(news.getExpiryDateTime().isBefore(LocalDate.now())){
                newsRepository.delete(news);
            }
        });
    }
}
