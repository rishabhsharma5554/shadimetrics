package com.shaadimetrics.website.config;

import com.shaadimetrics.website.domain.*;
import com.shaadimetrics.website.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Bootstraps the database on first run: a super-admin account plus the original
 * site content (gallery photos, testimonials, services, sample offers) so the
 * admin panel has something real to manage from day one.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AppUserRepository userRepository;
    private final GalleryImageRepository galleryImageRepository;
    private final TestimonialRepository testimonialRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final OfferRepository offerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username}")
    private String seedAdminUsername;

    @Value("${app.seed.admin-password}")
    private String seedAdminPassword;

    public DataSeeder(AppUserRepository userRepository,
                       GalleryImageRepository galleryImageRepository,
                       TestimonialRepository testimonialRepository,
                       ServiceItemRepository serviceItemRepository,
                       OfferRepository offerRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.galleryImageRepository = galleryImageRepository;
        this.testimonialRepository = testimonialRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.offerRepository = offerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedGallery();
        seedTestimonials();
        seedServices();
        seedOffers();
    }

    private void seedUsers() {
        if (userRepository.existsByUsernameIgnoreCase(seedAdminUsername)) {
            return;
        }
        AppUser admin = new AppUser();
        admin.setUsername(seedAdminUsername);
        admin.setPassword(passwordEncoder.encode(seedAdminPassword));
        admin.setFullName("Shaadi Metrics Admin");
        admin.setRoles(EnumSet.of(Role.SUPER_ADMIN));
        userRepository.save(admin);
        log.info("Seeded default super-admin user '{}'. Change this password after first login!", seedAdminUsername);
    }

    private void seedGallery() {
        if (galleryImageRepository.count() > 0) {
            return;
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        int total = 0;
        for (GalleryCategory category : GalleryCategory.values()) {
            try {
                Resource[] resources = resolver.getResources(
                        "classpath:/static/images/gallery/" + category.getSlug() + "/*");
                List<Resource> sorted = Arrays.stream(resources)
                        .sorted(Comparator.comparing(Resource::getFilename))
                        .toList();
                int order = 0;
                for (Resource resource : sorted) {
                    GalleryImage image = new GalleryImage();
                    image.setCategory(category);
                    image.setImagePath("/images/gallery/" + category.getSlug() + "/" + resource.getFilename());
                    image.setSortOrder(order++);
                    image.setFeatured(order <= 3);
                    galleryImageRepository.save(image);
                    total++;
                }
            } catch (IOException e) {
                log.warn("Could not seed gallery category {}: {}", category, e.getMessage());
            }
        }
        log.info("Seeded {} gallery images across {} categories.", total, GalleryCategory.values().length);
    }

    private void seedTestimonials() {
        if (testimonialRepository.count() > 0) {
            return;
        }
        record Seed(String couple, String location, String quote, String photo) {}
        List<Seed> seeds = List.of(
                new Seed("Gaurav & Manpreet", "Mandsaur",
                        "We are truly grateful for the way our wedding was planned and executed. Every function had its own beautiful setup and the coordination was excellent.",
                        "/images/site/testimonial-1.jpg"),
                new Seed("Vikram & Sambhavna", "Indore",
                        "Our wedding functions were managed so well. Decoration was nice and everything was done on time. Family members loved the arrangements.",
                        "/images/site/testimonial-2.jpg"),
                new Seed("Ankit & Apurva", "Indore",
                        "Everything was managed beautifully. Decor and arrangements were exactly like we discussed. The team was cooperative throughout every function.",
                        "/images/site/testimonial-3.png"),
                new Seed("Ayush & Shrishti", "Indore",
                        "Very nice experience working with the team. All functions were handled properly and decoration looked beautiful. Happy with the planning!",
                        "/images/site/testimonial-4.jpg"),
                new Seed("Aditya & Aishwarya", "Amravati",
                        "Our wedding was organized very nicely. The team handled everything from decoration to guest coordination - we could enjoy every function peacefully.",
                        "/images/site/testimonial-1.jpg"),
                new Seed("Rishabh & Prachi", "Indore",
                        "Very good management during our wedding functions. Decor was beautiful and everything was on time - polite team, wonderful overall experience.",
                        "/images/site/testimonial-2.jpg")
        );
        int order = 0;
        for (Seed seed : seeds) {
            Testimonial testimonial = new Testimonial();
            testimonial.setCoupleNames(seed.couple());
            testimonial.setLocation(seed.location());
            testimonial.setQuote(seed.quote());
            testimonial.setPhotoPath(seed.photo());
            testimonial.setSortOrder(order++);
            testimonial.setStatus(com.shaadimetrics.website.domain.ReviewStatus.APPROVED);
            testimonial.setSubmittedBy("system");
            testimonialRepository.save(testimonial);
        }
        log.info("Seeded {} testimonials.", seeds.size());
    }

    private void seedServices() {
        if (serviceItemRepository.count() > 0) {
            return;
        }
        record Seed(String title, String desc, String image) {}
        List<Seed> seeds = List.of(
                new Seed("Venue Selection", "Handpicked venues across Indore & MP to match your style, budget, and guest count.", "/images/site/service-venue.jpg"),
                new Seed("Decoration & Setup", "From royal mandaps to intimate floral settings - designs that take your breath away.", "/images/site/service-decor.jpg"),
                new Seed("Entertainment & Events", "DJs, live bands, choreographers, and anchors for sangeet, mehendi, and reception.", "/images/site/service-entertainment.jpg"),
                new Seed("Photography & Videography", "Cinematic storytelling through the lens - capturing every emotion, every glance.", "/images/site/service-photography.png"),
                new Seed("Invitation Cards", "Elegant digital and physical invitations that set the tone for your celebration.", "/images/site/service-invitations.jpg"),
                new Seed("Accommodation", "Comfortable guest stays, airport transfers, and hospitality management.", "/images/site/service-accommodation.jpeg")
        );
        int order = 0;
        for (Seed seed : seeds) {
            ServiceItem item = new ServiceItem();
            item.setTitle(seed.title());
            item.setDescription(seed.desc());
            item.setImagePath(seed.image());
            item.setSortOrder(order++);
            serviceItemRepository.save(item);
        }
        log.info("Seeded {} services.", seeds.size());
    }

    private void seedOffers() {
        if (offerRepository.count() > 0) {
            return;
        }
        Offer earlyBird = new Offer();
        earlyBird.setTitle("Early Bird Booking");
        earlyBird.setDescription("Book your wedding planning package 6 months in advance and lock in this season's best rate.");
        earlyBird.setCouponCode("EARLYBIRD10");
        earlyBird.setDiscountText("Flat 10% Off");
        earlyBird.setValidFrom(LocalDate.now());
        earlyBird.setValidTo(LocalDate.now().plusMonths(6));
        earlyBird.setImagePath("/images/gallery/mandap/01.jpeg");
        offerRepository.save(earlyBird);

        Offer festive = new Offer();
        festive.setTitle("Festive Season Special");
        festive.setDescription("Sign a full-service package this festive season and get complimentary invitation card design.");
        festive.setCouponCode("FESTIVE2026");
        festive.setDiscountText("Free Invitations");
        festive.setValidFrom(LocalDate.now());
        festive.setValidTo(LocalDate.now().plusMonths(3));
        festive.setImagePath("/images/gallery/sangeet/01.jpeg");
        offerRepository.save(festive);

        Offer referral = new Offer();
        referral.setTitle("Refer a Couple");
        referral.setDescription("Refer a friend or family member who books with us and you both get a thank-you credit toward your décor budget.");
        referral.setCouponCode("REFER5000");
        referral.setDiscountText("₹5,000 Credit");
        referral.setValidFrom(LocalDate.now());
        referral.setImagePath("/images/gallery/reception/01.jpeg");
        offerRepository.save(referral);

        log.info("Seeded 3 sample offers.");
    }
}
