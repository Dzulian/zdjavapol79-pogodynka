package pl.sdacademy.pogodynka.repository;

import org.apache.commons.lang3.StringUtils;
import pl.sdacademy.pogodynka.repository.api.openweathermap.model.dao.WeatherMapCityEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.List;
import java.util.Optional;

public class WeatherMapCityRepository {

    private static final WeatherMapCityRepository INSTANCE = new WeatherMapCityRepository();

    public static WeatherMapCityRepository getInstance(){
        return INSTANCE;
    }

    public void saveToDatabase(List<WeatherMapCityEntity> cities) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("weatherAppPU");
        final EntityManager em = emf.createEntityManager();
        try {

            em.getTransaction().begin();
            cities.forEach(em::merge);
            em.getTransaction().commit();

        } finally {
            emf.close();
        }
    }

    public Optional<Long> getIdOfCity(String city) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("weatherAppPU");
        final EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select city from WeatherMapCityEntity city where city.id = :numericId or :query member of city.keyWords");
            query.setParameter("query", StringUtils.stripAccents(city)); //https://stackoverflow.com/questions/3322152/is-there-a-way-to-get-rid-of-accents-and-convert-a-whole-string-to-regular-lette
            query.setParameter("numericId", city.matches("-?\\d+") ? Long.parseLong(city) : -1);
            return Optional.ofNullable((List<WeatherMapCityEntity>)query.getResultList()).filter(x -> x.size() > 0).map(x -> x.get(0).getId());
        } finally {
            emf.close();
        }
    }
}
