package com.ghoome.pages;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.ghoome.entity.Person;
import com.ghoome.entity.Person.Sex;
import com.ghoome.repository.PersonRepository;

public class Index {
	@Inject
	private PersonRepository repository;
	private Person dave, oliver, carter, boyd, stefan, leroi, alicia;
	
	Object onActionFromIndexForm(){
		repository.deleteAll();

		dave = new Person("Dave", "Matthews", 42);
		oliver = new Person("Oliver August", "Matthews", 4);
		carter = new Person("Carter", "Beauford", 49);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		boyd = new Person("Boyd", "Tinsley", 45);
		stefan = new Person("Stefan", "Lessard", 34);
		leroi = new Person("Leroi", "Moore", 41);
		alicia = new Person("Alicia", "Keys", 30, Sex.FEMALE);

		repository.save(Arrays.asList(oliver, dave, carter, boyd, stefan, leroi, alicia));
		
		List<Person> result = repository.findAll();
		for(Person p:result){
			System.out.println("person: "+p.toString());
		}
		
		return this;
	}
}
