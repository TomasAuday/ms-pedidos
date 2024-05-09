package dan.ms.tp.mspedidos.filter;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dan.ms.tp.mspedidos.dto.auth.UserInfo;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class JwtFilter implements Filter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter() {
        super();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            String token = "";
            if (request.getHeader("Authorization").startsWith("Bearer ")) {
                token = request.getHeader("Authorization").substring(7);
            }

            UserInfo userInfo = extractUserInfoFromToken(token);

            UserInfoContextHolder.setUser(userInfo);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            UserInfoContextHolder.setUser(null);
            // Continue without an user...
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private UserInfo extractUserInfoFromToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        // [0] its the header: header.payload.sign
        String payload = new String(decoder.decode(chunks[1]));

        try {
            UserPayload userPayload = objectMapper.readValue(payload, UserPayload.class);

            String userName = userPayload.sub;
            Integer idTipoUsuario = userPayload.tipo.id;
            String tipoUsuario = userPayload.tipo.tipo;

            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setIdTipoUsuario(idTipoUsuario);
            userInfo.setTipoUsuario(tipoUsuario);

            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties
    static class UserPayload {
        String sub;
        Tipo tipo;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonIgnoreProperties
        static class Tipo {
            Integer id;
            String tipo;
        }
    }

}