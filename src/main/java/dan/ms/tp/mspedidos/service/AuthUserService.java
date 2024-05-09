package dan.ms.tp.mspedidos.service;


import dan.ms.tp.mspedidos.dto.auth.UserInfo;


public interface AuthUserService {
    UserInfo getCurrentUser();
}
