package com.example.demo;


import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Role;
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

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.*;


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

    public void testGetById() {
        User user = generateUser();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        ResponseEntity<User> responseEntity = userController.findById(1L);
        User responseUser = responseEntity.getBody();
        Assert.assertEquals(user.getUsername(), responseUser.getUsername());
        Assert.assertEquals(user.getPassword(), responseUser.getPassword());
    }

    @Test

    public void testBadGetId() {
        ResponseEntity<User> responseEntity = userController.findById(1L);
        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test

    public void testGetByUserName() {
        User user = generateUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.findByUserName(user.getUsername());
        User responseUser = responseEntity.getBody();
        Assert.assertEquals(user.getUsername(), responseUser.getUsername());
        Assert.assertEquals(user.getPassword(), responseUser.getPassword());
    }

    @Test

    public void testGetAllUser(){
        User user = generateUser();
        Role userRole = new Role();
        userRole.setName(RoleEnum.User);
        when(roleRepository.getByName(RoleEnum.User)).thenReturn(userRole);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findAllByRolesContaining(userRole)).thenReturn(userList);
        ResponseEntity<List<User>> responseEntity = userController.getAllUser();
        List<User> userList1 = responseEntity.getBody();
        Assert.assertEquals(userList1.get(0).getPassword(), userList.get(0).getPassword());
        Assert.assertEquals(userList1.get(0).getUsername(), userList.get(0).getUsername());
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
