package com.example.demo;


import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.RoleEnum;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.RoleRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;





    @Before
    public void setUp() {


    }

    @Test
    public void testRightCreateUser(){
        CreateUserRequest createUserRequest = generateCUR(RoleEnum.User);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        User user = responseEntity.getBody();
        Assert.assertEquals(user.getUsername(), createUserRequest.getUsername());
        Assert.assertEquals(user.getId(), 0);
    }

    @Test
    public void testBadRequestCreateUser() {
        CreateUserRequest createUserRequest = generateCUR(null);
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        Assert.assertEquals(400, responseEntity.getStatusCodeValue());

        createUserRequest.setPassword("11");
        ResponseEntity<User> responseEntity1 = userController.createUser(createUserRequest);
        Assert.assertEquals(400, responseEntity1.getStatusCodeValue());
    }

    @Test
    @WithMockUser(authorities = {"User"})
    public void testGetById() {
        User user = generateUser();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        ResponseEntity<User> responseEntity = userController.findById(1L);
        User responseUser = responseEntity.getBody();
        Assert.assertEquals(user.getUsername(), responseUser.getUsername());
        Assert.assertEquals(user.getPassword(), responseUser.getPassword());
    }





    public User generateUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        return user;
    }

    public CreateUserRequest generateCUR(RoleEnum roleEnum) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        Set<RoleEnum> set = new HashSet<>();
        set.add(roleEnum);
        createUserRequest.setRoles(set);
        return createUserRequest;
    }


}
