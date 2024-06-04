package springServer.databaseOperations;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springServer.businessLogic.Place;
import springServer.users.User;

@RestController
final class OffersController {
    @Value("${mongodb.uri}")
    private String mongoUri;

    @PostMapping("/add-offer")
    ResponseEntity<String> addOffer (@RequestBody String body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2User oauthUser) {
                String username = oauthUser.getAttribute("given_name");
                String email = oauthUser.getAttribute("email");
                String profilePictureUrl = oauthUser.getAttribute("picture");
                User actUser = new User(username, email, profilePictureUrl);
                Place place = Place.fromJson(new JSONObject(body));
                OfferIntoDb offerIntoDb = new OfferIntoDb(mongoUri);
                offerIntoDb.insertOfferIntoDb(place, actUser);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(
                        "User is not logged in",
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-offers")
    ResponseEntity<String> allOffers() {
        try {
            OfferIntoDb myObj = new OfferIntoDb(mongoUri);
            String jsonStrRes = myObj.getAllOffers();
            return new ResponseEntity<>(jsonStrRes, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-offers")
    ResponseEntity<String> myOffers() {
        try {
            OfferIntoDb myObj = new OfferIntoDb(mongoUri);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2User oauthUser) {
                String email = oauthUser.getAttribute("email");
                String result = myObj.getMyOffers(email);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else { // Nigdy się nie wykona - endpont jest zabezpieczony
                return new ResponseEntity<>("User is not logged in", HttpStatus.UNAUTHORIZED);
            }
        } catch (RuntimeException e) {
          e.printStackTrace();
          return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
