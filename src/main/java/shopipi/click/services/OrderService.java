package shopipi.click.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.repositories.OrderRepo;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepo orderRepo;

}
