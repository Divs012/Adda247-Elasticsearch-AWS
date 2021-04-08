package com.divya.jest.controller;

import com.divya.jest.model.Employee;
import com.divya.jest.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    JestClient client =null;
    public JestClient getClient() {
        if(this.client==null)
        {
            System.out.println("setting up connection with jedis");
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(
                    new HttpClientConfig.Builder("https://search-ytsearch-staging-vflomzxcm3c4pklej6nwyomxfm.us-east-1.es.amazonaws.com")
                            .multiThreaded(true)
                            .defaultMaxTotalConnectionPerRoute(2)
                            .maxTotalConnection(10)
                            .build());
            this.client=factory.getObject();
            return factory.getObject();
        }
        return this.client;


    }

    @PostMapping("/save")
    public String saveCustomer(@RequestBody Employee employee) throws IOException {

        JestClient client = this.getClient();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode userNode = mapper.createObjectNode()
                .put("name", employee.getName())
                .put("department", employee.getDepartment())
                .put("salary", employee.getSalary());
        JestResult postResult = client.execute(new Index.Builder(userNode.toString()).index("data").type("employee").build());

        return postResult.toString();
    }

    @GetMapping("/find/{id}")
    public String findEmployee(@PathVariable final String id) throws IOException {
        JestClient client = this.getClient();

        JestResult getResult = client.execute(new Get.Builder("data",id).type("employee").build());
        return getResult.toString();
    }

    @PutMapping("/update/{id}")
    public String updateEmployee(@PathVariable final String id ,@RequestBody Employee employee) throws IOException
    {
        JestClient client = this.getClient();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode userNode = mapper.createObjectNode()
                .put("name", employee.getName())
                .put("department", employee.getDepartment())
                .put("salary", employee.getSalary());
        JestResult putResult  = client.execute(new Update.Builder(userNode.toString()).index("data").id(id).build());

        return putResult.toString();
    }
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable final String id, @RequestBody Employee employee )throws IOException
    {
        JestClient client = this.getClient();

        JestResult deleteResult = client.execute(new Delete.Builder(id).index("data").type("employee").build());
        return deleteResult.toString();
    }
}
