package com.ghoome.pages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.apache.xpath.objects.XObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.ghoome.entity.Address;
import com.ghoome.entity.Person;
import com.ghoome.entity.User;
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
		long start = System.currentTimeMillis();
		for(int i=0;i<1*10000;i++){
			User user = new User("chinaz"+i);
			template.save(user);
			Person p = new Person();
			p.setCreator(user);
			Set<Address> sas = new HashSet<Address>();
			for(int j=0;j<3;j++){
				sas.add(new Address("street-"+Math.random(),
						"zipcode-"+Math.random(),"city-"+Math.random()));
			}
			sas.add(new Address("shenzhen","10000","guangdong"));
			p.setShippingAddresses(sas);
			p.setAddress(new Address("street-main","zipcode-main","city-main"));
			p.setAge(20);
			p.setEmail("a"+i+"@b.c");
			p.setFirstname("a"+i);
			p.setLastname("b");
			p.setLocation(new Point(22,22));
			
			repository.save(p);
		}
		
		long end = System.currentTimeMillis();
		System.out.println("total: "+(end-start));
		
		return this;
	}
	
	Object onActionFromQueryPerson(){
		Query q = new Query();
		q.addCriteria(new Criteria("lastname").is("b"));
		//q.fields().include("shippingAddresses").include("creator");
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
	
	Object onActionFromUpdatePerson(){
		Query q = new Query();
		q.addCriteria(new Criteria("lastname").is("b"));
		//q.fields().include("shippingAddresses").include("creator");
		//Person p = (Person)template.findOne(q, Person.class);
		template.updateMulti(q, new Update().set("firstname", "fa"), Person.class);
		return this;
	}
	
	Object onActionFromGroupPerson(){
		GroupByResults<Person> results = template.group(
				"person",
				GroupBy.keyFunction("function(doc) { return { email : doc.email }; }").initialDocument("{ count: 0 }")
						.reduceFunction("function(doc, prev) { prev.count += 1 }"), Person.class);
		
		System.out.println("raw: "+results.getRawResults());
		
		Iterator<Person> pit = results.iterator();
		while(pit.hasNext()){
			Person p = pit.next();
			System.out.println("p: "+p);
		}
		return this;
	}
}
