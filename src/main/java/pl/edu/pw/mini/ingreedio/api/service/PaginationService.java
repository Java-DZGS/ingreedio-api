package pl.edu.pw.mini.ingreedio.api.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PaginationService {
    @Value("${pagination.page-size}")
    private int pageSize;

    public PageRequest getPageRequest(Optional<Integer> pageNumber) {
        return PageRequest.of(pageNumber.orElse(0), pageSize);
    }
}
