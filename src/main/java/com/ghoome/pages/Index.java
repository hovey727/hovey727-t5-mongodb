package com.ghoome.pages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ghoome.entity.Address;
import com.ghoome.entity.Person;
import com.ghoome.repository.PersonRepository;

public class Index {
	@Inject
	private PersonRepository repository;
	@Inject
	private MongoTemplate template;
	
	Object onActionFromIndexForm(){
		repository.deleteAll();
		return this;
	}
	
	Object onActionFromAddPerson(){
		Person p = new Person();
		Set<Address> sas = new HashSet<Address>();
		for(int j=0;j<3;j++){
			sas.add(new Address("street-"+Math.random(),
					"zipcode-"+Math.random(),"city-"+Math.random()));
		}
		sas.add(new Address("shenzhen","10000","guangdong"));
		p.setShippingAddresses(sas);
		p.setAddress(new Address("street-main","zipcode-main","city-main"));
		p.setAge(20);
		p.setEmail("a@b.c");
		p.setFirstname("li");
		p.setLastname("wei");
		p.setLocation(new Point(22,22));
		
		repository.save(p);
		
		return this;
	}
	
	Object onActionFromQueryPerson(){
		Query q = new Query();
		q.addCriteria(new Criteria("lastname").is("wei"));
		q.fields().include("shippingAddresses");
		Person p = (Person)template.findOne(q, Person.class);
		
		System.out.println("person: "+p);
		
		Set<Address> sas = p.getShippingAddresses();
		Iterator<Address> it = sas.iterator();
		while(it.hasNext()){
			Address add = it.next();
			System.out.println(
					"street: "+add.getStreet()
					+",zip: "+add.getZipCode()
					+",city: "+add.getCity());
		}
		
		return this;
	}
}
