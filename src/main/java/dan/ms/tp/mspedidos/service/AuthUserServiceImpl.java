package dan.ms.tp.mspedidos.service;

import org.springframework.stereotype.Service;

import dan.ms.tp.mspedidos.dto.auth.UserInfo;
import dan.ms.tp.mspedidos.filter.UserInfoContextHolder;

@Service
public class AuthUserServiceImpl implements AuthUserService{
    @Override
    public UserInfo getCurrentUser() {
        return UserInfoContextHolder.getUser(); 
    }
}
