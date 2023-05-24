package org.acme.quarkussocial.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import org.acme.quarkussocial.domain.model.Follower;
import org.acme.quarkussocial.domain.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        //metodo para que a mesmo seguidor nao siga 2 vezes o mesmo ID
//        Map<String, Object> params = new HashMap<>();
//        params.put("follower", follower);
//        params.put("user", user);
        //o quarkus tem um metodo parameters que faz o mesmo q de cima.
        var params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Follower> query = find("follower =:follower and user =:user ", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    //buscando a lista de usuario com seus seguidores.
    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters.with("userId", userId).and("followerId", followerId).map();

        delete("follower.id =:followerId and user.id =:userId", params);
    }
}
