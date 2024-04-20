package lk.ijse.spring.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.spring.dto.CustomDTO;
import lk.ijse.spring.dto.OrderDTO;
import lk.ijse.spring.dto.OrderDetailsDTO;
import lk.ijse.spring.entity.Item;
import lk.ijse.spring.entity.OrderDetails;
import lk.ijse.spring.entity.Orders;
import lk.ijse.spring.repo.ItemRepo;
import lk.ijse.spring.repo.OrderDetailsRepo;
import lk.ijse.spring.repo.PlaceOrderRepo;
import lk.ijse.spring.service.PlaceOrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Transactional
public class PlaceOrderServiceImpl implements PlaceOrderService {
    @Autowired
    private PlaceOrderRepo repo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private OrderDetailsRepo orRepo;

    @Autowired
    private ModelMapper mapper;


    @Override
    public void placeOrder(OrderDTO dto) {
        Orders orders = mapper.map(dto, Orders.class);
        if (repo.existsById(orders.getOid())){
            throw new RuntimeException("Order" + orders.getOid() + " Already added.!");
        }
        repo.save(orders);

        for (OrderDetails od : orders.getOrderDetails()){
            Item item = itemRepo.findById(od.getItemCode()).get();
            item.setQty(item.getQty() - od.getQty());
            itemRepo.save(item);
        }
    }

    @Override
    public ArrayList<OrderDTO> LoadOrders() {
        return mapper.map(repo.findAll(), new TypeToken<ArrayList<OrderDTO>>() {
        }.getType());
    }

    @Override
    public ArrayList<OrderDetailsDTO> LoadOrderDetails() {
        return mapper.map(orRepo.findAll(), new TypeToken<ArrayList<OrderDetailsDTO>>() {
        }.getType());
    }

    @Override
    public CustomDTO OrderIdGenerate() {
        return new CustomDTO(repo.getLastIndex());
    }

    @Override
    public CustomDTO getSumOrders() {
        return new CustomDTO(repo.getSumOrders());
    }
}
