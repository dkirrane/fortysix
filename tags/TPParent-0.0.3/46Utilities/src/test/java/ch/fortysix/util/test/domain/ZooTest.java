package ch.fortysix.util.test.domain;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import ch.fortysix.test.domain.Zoo;

@ContextConfiguration(locations = { "classpath:/default-context.xml" })
public class ZooTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	Zoo zoo;

	@Test
	public void testShowAnimals() throws Exception {
		assertNotNull(zoo);
		assertNotNull("monsters", zoo.getMonsters());
		assertNotNull("mouses", zoo.getMouses());
		zoo.showAnimals();
	}
}
