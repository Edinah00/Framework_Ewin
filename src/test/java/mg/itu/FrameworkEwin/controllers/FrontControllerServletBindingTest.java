package mg.itu.FrameworkEwin.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

class FrontControllerServletBindingTest {

    public static class SamplePerson {
        private String nom;
        private String mail;
        private int age;

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getMail() { return mail; }
        public void setMail(String mail) { this.mail = mail; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @Test
    void shouldBindNestedFormValuesToObjectParameter() throws Exception {
        Map<String, String[]> params = Map.of(
            "p.nom", new String[] {"Alice"},
            "p.mail", new String[] {"alice@example.com"},
            "p.age", new String[] {"32"}
        );

        Method bindMethod = FrontControllerServlet.class.getDeclaredMethod("bindParameterValue", String.class, Class.class, Map.class);
        bindMethod.setAccessible(true);
        SamplePerson person = (SamplePerson) bindMethod.invoke(null, "p", SamplePerson.class, params);

        assertEquals("Alice", person.getNom());
        assertEquals("alice@example.com", person.getMail());
        assertEquals(32, person.getAge());
    }
}
