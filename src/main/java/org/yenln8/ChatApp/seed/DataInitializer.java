package org.yenln8.ChatApp.seed;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.event.synchronize.SynchronizeUserEvent;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Slf4j
public class DataInitializer {
    @Value("${app.redis.streams.sync-stream}")
    private String syncStream;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private NativeLanguageRepository nativeLanguageRepository;
    @Autowired
    private LearningLanguageRepository learningLanguageRepository;
    @Autowired
    private LimitResourceRepository limitResourceRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private NativeLanguageLocaleRepository nativeLanguageLocaleRepository;
    @Autowired
    private LearningLanguageLocaleRepository learningLanguageLocaleRepository;
    @Autowired
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TopicTestRepository topicTestRepository;
    @Autowired
    private DifficultyTestRepository difficultyTestRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private QuestionTestRepository questionTestRepository;
    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Bean
    @Transactional
    public CommandLineRunner seed(UserRepository userRepository) {
        return args -> {
            this.seedLearningNative();
            this.seedAttachment();
            this.seedLastOnline();
            this.seedUser();
            this.seedTopic();
            this.seedDifficulty();
            questionOptionRepository.deleteAll();
            questionTestRepository.deleteAll();
            testRepository.deleteAll();
            this.seedTest();
        };
    }

    public void seedTest() {

        var topicFamily = topicTestRepository.findById(1L).get();     // Family
        var topicWork = topicTestRepository.findById(2L).get();       // Work
        var topicEducation = topicTestRepository.findById(3L).get();  // Education

        var English = learningLanguageRepository.findById(1L).get();
        var VietNam = learningLanguageRepository.findById(2L).get();
        var Spanish = learningLanguageRepository.findById(3L).get();
        var French = learningLanguageRepository.findById(4L).get();
        var German = learningLanguageRepository.findById(5L).get();
        var Chinese = learningLanguageRepository.findById(6L).get();
        var Japanese = learningLanguageRepository.findById(7L).get();
        var Korean = learningLanguageRepository.findById(8L).get();
        var Hindi = learningLanguageRepository.findById(9L).get();
        var Russian = learningLanguageRepository.findById(10L).get();
        var Portuguese = learningLanguageRepository.findById(11L).get();
        var Arabic = learningLanguageRepository.findById(12L).get();
        var Italian = learningLanguageRepository.findById(13L).get();
        var Turkish = learningLanguageRepository.findById(14L).get();
        var Dutch = learningLanguageRepository.findById(15L).get();
        var Thai = learningLanguageRepository.findById(16L).get();

        try {
            // ===== FAMILY =====
            seedForFamilyEnglish(topicFamily, English);//
            seedForFamilyVietNam(topicFamily, VietNam);//
            seedForFamilySpanish(topicFamily, Spanish);//
            seedForFamilyFrench(topicFamily, French);//
            seedForFamilyGerman(topicFamily, German);//
            seedForFamilyChinese(topicFamily, Chinese);//
            seedForFamilyJapanese(topicFamily, Japanese);//
            seedForFamilyKorean(topicFamily, Korean);//
            seedForFamilyHindi(topicFamily, Hindi);
            seedForFamilyRussian(topicFamily, Russian);
            seedForFamilyPortuguese(topicFamily, Portuguese);
            seedForFamilyArabic(topicFamily, Arabic);
            seedForFamilyItalian(topicFamily, Italian);
            seedForFamilyTurkish(topicFamily, Turkish);
            seedForFamilyDutch(topicFamily, Dutch);
            seedForFamilyThai(topicFamily, Thai);

            // ===== WORK =====
            seedForWorkEnglish(topicWork, English);
            seedForWorkVietNam(topicWork, VietNam);

            seedForWorkSpanish(topicWork, Spanish);
            seedForWorkFrench(topicWork, French);
            seedForWorkGerman(topicWork, German);
            seedForWorkChinese(topicWork, Chinese);
            seedForWorkJapanese(topicWork, Japanese);
            seedForWorkKorean(topicWork, Korean);
            seedForWorkHindi(topicWork, Hindi);
            seedForWorkRussian(topicWork, Russian);
            seedForWorkPortuguese(topicWork, Portuguese);
            seedForWorkArabic(topicWork, Arabic);
            seedForWorkItalian(topicWork, Italian);
            seedForWorkTurkish(topicWork, Turkish);
            seedForWorkDutch(topicWork, Dutch);
            seedForWorkThai(topicWork, Thai);

            // ===== EDUCATION =====
            seedForEducationEnglish(topicEducation, English);
            seedForEducationVietNam(topicEducation, VietNam);

            seedForWorkSpanish(topicWork, Spanish);
            seedForWorkFrench(topicWork, French);
            seedForWorkGerman(topicWork, German);
            seedForWorkChinese(topicWork, Chinese);
            seedForWorkJapanese(topicWork, Japanese);
            seedForWorkKorean(topicWork, Korean);
            seedForWorkHindi(topicWork, Hindi);
            seedForWorkRussian(topicWork, Russian);
            seedForWorkPortuguese(topicWork, Portuguese);
            seedForWorkArabic(topicWork, Arabic);
            seedForWorkItalian(topicWork, Italian);
            seedForWorkTurkish(topicWork, Turkish);
            seedForWorkDutch(topicWork, Dutch);
            seedForWorkThai(topicWork, Thai);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String[][] EnglishFamilyEasy() {
        return new String[][] {
                {"My Family Tree", "Mastering basic kinship terms and standard English titles"},
                {"What do you call your father's sister?", "Aunt", "Niece", "Cousin", "Sister-in-law", "Aunt", "Your parents' sisters are your aunts, and their brothers are your uncles."},
                {"Your brother's daughter is your...", "Niece", "Nephew", "Cousin", "Step-sister", "Niece", "A female child of your sibling is a niece; a male child is a nephew."},
                {"What is the term for a family consisting only of parents and their children?", "Extended family", "Nuclear family", "Joint family", "Single family", "Nuclear family", "A nuclear family is the core unit: two parents and their immediate offspring."},
                {"When a man gets married, the mother of his wife becomes his...", "Step-mother", "Godmother", "Mother-in-law", "Grandmother", "Mother-in-law", "In English, 'in-law' is added to relatives gained through marriage."},
                {"What do you call your male child?", "Daughter", "Son", "Brother", "Nephew", "Son", "A male child is a son; a female child is a daughter."},
                {"Who is your 'first cousin'?", "The child of your aunt or uncle", "The child of your brother", "The brother of your father", "Your step-brother", "The child of your aunt or uncle", "First cousins share the same grandparents."},
                {"If your father marries again, his new wife is your...", "Aunt", "Mother-in-law", "Step-mother", "Ex-mother", "Step-mother", "'Step' relations are created when a parent remarries."},
                {"What do you call the person you are engaged to be married to?", "Spouse", "Fiancé / Fiancée", "Partner", "In-law", "Fiancé / Fiancée", "Fiancé is for a man; Fiancée is for a woman."},
                {"What is a 'Sibling'?", "Your cousin", "Your brother or sister", "Your twin only", "Your parent", "Your brother or sister", "Sibling is a gender-neutral term for brothers and sisters."},
                {"What do you call your father's father?", "Great-grandfather", "Grandfather", "Uncle", "Step-father", "Grandfather", "Grandfather is the father of one of your parents."},
                {"What is the plural of 'Child'?", "Childs", "Children", "Childrens", "Kids", "Children", "Child is singular; children is the irregular plural form."},
                {"What is an 'Only Child'?", "A child with no siblings", "The oldest child", "The youngest child", "A child with one twin", "A child with no siblings", "An only child is someone with no brothers or sisters."},
                {"Who is your 'Spouse'?", "Your sibling", "Your husband or wife", "Your parent", "Your neighbor", "Your husband or wife", "Spouse is a formal term for the person you are married to."},
                {"What do you call your son's son?", "Nephew", "Great-son", "Grandson", "Cousin", "Grandson", "Your child's child is your grandchild (grandson/granddaughter)."},
                {"What is a 'Step-brother'?", "The son of your step-parent", "Your biological brother", "Your brother-in-law", "Your cousin", "The son of your step-parent", "A step-brother is not related by blood but through a parent's marriage."},
                {"What do you call the male child of your sibling?", "Niece", "Nephew", "Cousin", "Grandson", "Nephew", "A nephew is the son of your brother or sister."},
                {"In a Western family, who are your 'Immediates'?", "Cousins and aunts", "Parents and siblings", "Neighbors", "Best friends", "Parents and siblings", "Immediate family usually refers to parents, spouses, and children."},
                {"What is a 'Widow'?", "A woman whose husband has died", "A woman who is divorced", "A woman who is not married", "A mother of twins", "A woman whose husband has died", "A widow is a woman who has lost her spouse; a man is a widower."},
                {"What do you call your mother's new husband who is not your biological father?", "Uncle", "Step-father", "Father-in-law", "Guardian", "Step-father", "He is your step-father through marriage."},
                {"What do you call your children's mother?", "Grandmother", "Aunt", "Wife", "Niece", "Wife", "If you are the father, the mother of your children is typically your wife or partner."}
        };
    }
    public static String[][] EnglishFamilyMedium() {
        return new String[][] {
            {"Family Life & Traditions", "Understanding idioms and common Western social customs"},
            {"What does the idiom 'The apple doesn't fall far from the tree' mean?", "Children are different from parents", "Children are similar to their parents", "Apples are good for families", "Families should live near trees", "Children are similar to their parents", "This means children often have similar qualities or talents as their parents."},
            {"In Western culture, what is a 'Family Reunion'?", "A wedding", "A gathering of extended family", "A parent-teacher meeting", "A legal divorce", "A gathering of extended family", "A reunion is when relatives who live apart meet up to celebrate."},
            {"What does it mean to be the 'Black Sheep' of the family?", "The favorite child", "The one who is different or a disgrace", "The oldest member", "The one who earns the most money", "The one who is different or a disgrace", "A 'black sheep' is someone who doesn't fit in or has brought shame to the family."},
            {"What is 'Breadwinner'?", "Someone who bakes bread", "The person who earns the money to support the family", "The youngest child", "A person who loves food", "The person who earns the money to support the family", "The breadwinner provides the primary income for the household."},
            {"In many English-speaking countries, what is a 'Baby Shower'?", "Washing a baby", "A party to give gifts to an expectant mother", "A baby's first bath", "A rainy day", "A party to give gifts to an expectant mother", "It is a celebration held before a baby is born."},
            {"What does 'Blood is thicker than water' mean?", "Water is more important", "Family bonds are stronger than any other", "Blood is hard to clean", "Friends are more important than family", "Family bonds are stronger than any other", "This proverb emphasizes that family loyalty comes first."},
            {"What is a 'Godparent'?", "A person who acts as a spiritual guardian", "A famous person", "Your biological parent", "A king or queen", "A person who acts as a spiritual guardian", "Godparents are chosen by parents to take an interest in the child's upbringing."},
            {"What does 'Like father, like son' mean?", "The father likes the son", "The son looks or acts like the father", "The father is taller than the son", "The son is older than the father", "The son looks or acts like the father", "It is used to point out similarities between a father and his son."},
            {"What is an 'Empty Nester'?", "A person with no home", "A bird", "Parents whose children have grown up and moved out", "A child living alone", "Parents whose children have grown up and moved out", "When children move out, the house is called an 'empty nest'."},
            {"What does the idiom 'To wear the pants in the family' mean?", "To buy clothes for everyone", "To be the person in control", "To be a fashion designer", "To work in a clothing store", "To be the person in control", "It refers to the person who makes the important decisions in a household."},
            {"What is a 'Stay-at-home dad'?", "A dad who is sick", "A dad who works from home", "A dad who stays home to raise children and manage the house", "A dad with no job", "A dad who stays home to raise children and manage the house", "This is a common modern role where the father handles domestic duties."},
            {"What does it mean to 'Follow in someone's footsteps'?", "To walk behind them", "To do the same job or life path as a family member", "To clean the house", "To go on a hike", "To do the same job or life path as a family member", "Often used when a child enters the same profession as their parent."},
            {"What is 'Flesh and blood'?", "A type of meal", "A distant relative", "A person's near relatives", "A scary movie", "A person's near relatives", "It refers to people who are biologically related to you."},
            {"In the West, what happens at a 'Golden Wedding Anniversary'?", "A couple celebrates 10 years of marriage", "A couple celebrates 50 years of marriage", "A person turns 50", "A new baby is born", "A couple celebrates 50 years of marriage", "Silver is 25 years, and Golden is 50 years."},
            {"What does it mean to 'Bring home the bacon'?", "To go grocery shopping", "To earn a living/income for the family", "To cook breakfast", "To be a farmer", "To earn a living/income for the family", "Similar to 'breadwinner', it means earning money for the family."},
            {"What is a 'Generation Gap'?", "A hole in the ground", "The difference in opinions between young and old people", "A long distance between houses", "A family photo", "The difference in opinions between young and old people", "It explains why children and parents sometimes disagree on trends or values."},
            {"What is 'Tough love'?", "Hating your family", "Being mean for no reason", "Acting sternly to help someone in the long run", "Fighting with siblings", "Acting sternly to help someone in the long run", "It involves setting firm rules to help a family member improve their behavior."},
            {"What is a 'Pet name'?", "A name for a dog", "A nickname used by family members or lovers", "A name for a cat", "A formal title", "A nickname used by family members or lovers", "Examples include 'Honey', 'Sweetie', or 'Pumpkin'."},
            {"In English, what are 'Grandparents' Day' or 'Mother's Day' examples of?", "Holidays to honor specific family members", "Religious festivals", "Government elections", "School vacations", "Holidays to honor specific family members", "These are widely celebrated days in Western culture."},
            {"What does the phrase 'Run in the family' mean?", "A family race", "A trait or ability that many family members have", "A family moving to a new house", "Escaping from home", "A trait or ability that many family members have", "For example: 'Musical talent runs in the family'."}
        };
    }public static String[][] EnglishFamilyHard() {
        return new String[][] {
                {"Heritage & Genealogy", "Exploring complex relations, legal terms, and ancestral history"},
                {"What does the term 'First cousin once removed' mean?", "The child of your first cousin", "Your cousin's ex-husband", "A second cousin", "A cousin who was kicked out", "The child of your first cousin", "'Once removed' indicates a one-generation difference between you and the cousin."},
                {"Who is an 'Ancestor'?", "A person you will have in the future", "A person from whom you are descended", "Your brother", "Your neighbor", "A person from whom you are descended", "Ancestors are usually those further back than grandparents."},
                {"What is 'Genealogy'?", "The study of plants", "The study of family history and lineages", "The study of the earth", "A type of medicine", "The study of family history and lineages", "It involves tracing one's family tree back through generations."},
                {"What does 'Next of kin' mean in a legal context?", "Your best friend", "Your closest living blood relative", "Your neighbor", "Your lawyer", "Your closest living blood relative", "The law uses this to identify who should be contacted in emergencies."},
                {"What is an 'Adoptive parent'?", "A parent who gives a child away", "A parent who legally takes a child as their own", "A biological parent", "A babysitter", "A parent who legally takes a child as their own", "Adoption creates a legal family bond without a biological one."},
                {"What is 'Primogeniture'?", "The right of the firstborn child to inherit the estate", "Moving to a new country", "Having many children", "A religious ceremony", "The right of the firstborn child to inherit the estate", "This was a common historical legal system in Europe."},
                {"What does it mean to be 'Estranged' from a family member?", "To be very close", "To no longer be on friendly terms or in contact", "To live in another country", "To be related by marriage", "To no longer be on friendly terms or in contact", "Estrangement occurs after a serious family conflict."},
                {"What is a 'Consanguineous' relationship?", "Related by marriage", "Related by blood", "Related by friendship", "Not related at all", "Related by blood", "This term is often used in legal and medical contexts."},
                {"Who is a 'Matriarch'?", "A male head of a family", "A female head of a family", "A young child", "A family lawyer", "A female head of a family", "A patriarch is a male leader; a matriarch is a female leader."},
                {"What is an 'Estate' in family law?", "A big house only", "The total property and money left by a person", "A family vacation", "A child's school", "The total property and money left by a person", "Settling an estate often happens after a family member passes away."},
                {"What does 'Lineage' refer to?", "The height of family members", "Direct descent from an ancestor", "The family's wealth", "The family's location", "Direct descent from an ancestor", "It is the line of descendants of a particular ancestor."},
                {"What is a 'Last Will and Testament'?", "A family story", "A legal document stating how to distribute assets after death", "A wedding invitation", "A birth certificate", "A legal document stating how to distribute assets after death", "This is a key document in family heritage and inheritance."},
                {"What is a 'Blended Family'?", "A family that cooks together", "A family with children from previous marriages", "A family with one child", "A family that lives in different cities", "A family with children from previous marriages", "It involves step-parents and step-siblings living as one unit."},
                {"What does the term 'Kith and Kin' mean?", "Only blood relatives", "Friends and family", "Enemies", "Strangers", "Friends and family", "'Kith' refers to acquaintances/friends, and 'kin' refers to family."},
                {"What is 'Co-parenting'?", "Two parents sharing the duties of raising a child after a split", "Parents working at the same job", "Parents having many children", "Grandparents raising kids", "Two parents sharing the duties of raising a child after a split", "Common among divorced or separated parents."},
                {"What is a 'Ward'?", "A person in charge of a family", "A minor under the legal care of a guardian", "An old family member", "A family doctor", "A minor under the legal care of a guardian", "A ward is protected by a guardian appointed by a court."},
                {"What does 'Heir' mean?", "The person who earns the most", "The person legally entitled to inherit property", "The air we breathe", "The oldest person in the town", "The person legally entitled to inherit property", "An heir receives the legacy or estate of an ancestor."},
                {"What is 'Alimony'?", "A family pet", "Payment made to a spouse after divorce", "A wedding gift", "A child's allowance", "Payment made to a spouse after divorce", "It is a legal financial support system between ex-spouses."},
                {"What does 'Ancestry' mean?", "A person's future children", "A person's ethnic or family origins", "A person's current job", "A person's hobby", "A person's ethnic or family origins", "Many people use websites to discover their ancestry."},
                {"What is a 'Great-aunt'?", "Your favorite aunt", "The sister of your grandparent", "The wife of your uncle", "The mother of your cousin", "The sister of your grandparent", "She is one generation above your parents' aunts."}
        };
    }
    private void seedForFamilyEnglish(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = EnglishFamilyEasy();
        var md = EnglishFamilyMedium();
        var h = EnglishFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] SpanishFamilyEasy() {
        return new String[][] {
                {"Mi Árbol Genealógico", "Dominando los términos básicos de parentesco en español"},
                {"¿Cómo llamas al hermano de tu padre?", "Tío", "Sobrino", "Primo", "Cuñado", "Tío", "El hermano de tu padre o madre es tu tío; la hermana es tu tía."},
                {"El hijo de tu hermano es tu...", "Sobrino", "Nieto", "Primo", "Hermanastro", "Sobrino", "El hijo de un hermano es un sobrino; la hija es una sobrina."},
                {"¿Cómo se llama a la familia formada solo por padres e hijos?", "Familia extendida", "Familia nuclear", "Familia conjunta", "Familia simple", "Familia nuclear", "La familia nuclear es la unidad básica: padres e hijos directos."},
                {"Cuando un hombre se casa, la madre de su esposa es su...", "Madrastra", "Madrina", "Suegra", "Abuela", "Suegra", "En español, los parientes por matrimonio forman la 'familia política'."},
                {"¿Cómo llamas a tu hijo varón?", "Hija", "Hijo", "Hermano", "Sobrino", "Hijo", "Un hijo es varón; una hija es mujer."},
                {"¿Quién es tu 'primo hermano'?", "El hijo de tu tío o tía", "El hijo de tu hermano", "El hermano de tu padre", "Tu hermanastro", "El hijo de tu tío o tía", "Los primos hermanos comparten los mismos abuelos."},
                {"Si tu padre se casa de nuevo, su nueva esposa es tu...", "Tía", "Suegra", "Madrastra", "Ex-madre", "Madrastra", "El prefijo 'madr-' o 'padr-' indica relación por nuevo matrimonio."},
                {"¿Cómo llamas a la persona con la que estás comprometido para casarte?", "Esposo", "Prometido / Prometida", "Pareja", "Pariente", "Prometido / Prometida", "Es la persona con la que tienes un compromiso formal de boda."},
                {"¿Qué son los 'Hermanos'?", "Tus primos", "Tus hermanos o hermanas", "Solo tus gemelos", "Tus padres", "Tus hermanos o hermanas", "Es el término general para los hijos de los mismos padres."},
                {"¿Cómo llamas al padre de tu padre?", "Bisabuelo", "Abuelo", "Tío", "Padrastro", "Abuelo", "El abuelo es el padre de uno de tus progenitores."},
                {"¿Cuál es el femenino de 'Yerno' (el esposo de tu hija)?", "Yerna", "Nuera", "Consuegra", "Hija", "Nuera", "Yerno es para el hombre, nuera es para la mujer."},
                {"¿Qué es un 'Hijo único'?", "Un niño sin hermanos", "El hijo mayor", "El hijo menor", "Un niño con un gemelo", "Un niño sin hermanos", "Alguien que no tiene hermanos ni hermanas."},
                {"¿Quién es tu 'Cónyuge'?", "Tu hermano", "Tu esposo o esposa", "Tu padre", "Tu vecino", "Tu esposo o esposa", "Es un término formal para la persona con la que estás casado."},
                {"¿Cómo llamas al hijo de tu hijo?", "Sobrino", "Bisnieto", "Nieto", "Primo", "Nieto", "El hijo de tu hijo o hija es tu nieto."},
                {"¿Qué es un 'Hermanastro'?", "El hijo de tu padrastro o madrastra", "Tu hermano biológico", "Tu cuñado", "Tu primo", "El hijo de tu padrastro o madrastra", "No comparten sangre, pero son hermanos por el matrimonio de sus padres."},
                {"¿Cómo llamas a la hija de tu hermano o hermana?", "Sobrina", "Sobrino", "Prima", "Nieta", "Sobrina", "La sobrina es la hija de un hermano o hermana."},
                {"¿Quiénes son tus 'Parientes'?", "Solo tus padres", "Todas las personas de tu familia", "Tus amigos cercanos", "Tus vecinos", "Todas las personas de tu familia", "Parientes incluye a tíos, primos, abuelos, etc."},
                {"¿Qué es una 'Viuda'?", "Una mujer cuyo esposo ha muerto", "Una mujer divorciada", "Una mujer soltera", "Una madre de gemelos", "Una mujer cuyo esposo ha muerto", "Si es hombre, se le llama viudo."},
                {"¿Cómo llamas al nuevo esposo de tu madre que no es tu padre biológico?", "Tío", "Padrastro", "Suegro", "Tutor", "Padrastro", "Es tu padrastro a través del matrimonio."},
                {"¿Cómo llamas a la madre de tus hijos?", "Abuela", "Tía", "Esposa", "Sobrina", "Esposa", "Si eres el padre, la madre de tus hijos suele ser tu esposa o pareja."}
        };
    }public static String[][] SpanishFamilyMedium() {
        return new String[][] {
                {"Vida y Tradiciones Familiares", "Entendiendo modismos y costumbres sociales hispanas"},
                {"¿Qué significa el modismo 'De tal palo, tal astilla'?", "Los hijos son diferentes a los padres", "Los hijos se parecen a sus padres", "La madera es buena", "La familia vive en el bosque", "Los hijos se parecen a sus padres", "Significa que los hijos heredan el carácter o costumbres de sus padres."},
                {"En la cultura hispana, ¿qué es un 'Padrino' o 'Madrina'?", "Un vecino", "Un guía y protector elegido en el bautizo", "Un médico", "El hermano mayor", "Un guía y protector elegido en el bautizo", "Tienen un papel muy importante en la vida y educación del niño."},
                {"¿Qué significa ser la 'Oveja negra' de la familia?", "El hijo favorito", "El que es diferente o causa problemas", "El miembro más viejo", "El que gana más dinero", "El que es diferente o causa problemas", "Alguien que no sigue las normas o tradiciones familiares."},
                {"¿Qué es el 'Cabeza de familia'?", "Alguien que vende sombreros", "La persona que toma las decisiones o sostiene el hogar", "El hijo menor", "Una persona que come mucho", "La persona que toma las decisiones o sostiene el hogar", "Tradicionalmente la figura de mayor autoridad."},
                {"¿Qué celebran muchas familias el 1 y 2 de noviembre?", "Navidad", "Día de los Muertos / Todos los Santos", "Pascua", "Independencia", "Día de los Muertos / Todos los Santos", "Es una tradición para recordar a los familiares fallecidos."},
                {"¿Qué significa 'La sangre tira'?", "Que la sangre es líquida", "Que los lazos familiares son muy fuertes", "Que no nos gusta la familia", "Que los amigos son más importantes", "Que los lazos familiares son muy fuertes", "Significa que siempre sentimos atracción o lealtad hacia nuestra familia."},
                {"¿Qué es un 'Compadre' o 'Comadre'?", "Un enemigo", "La relación entre los padres y los padrinos", "Un hermano biológico", "Un abuelo", "La relación entre los padres y los padrinos", "Es un vínculo social muy fuerte en la cultura latina."},
                {"¿Qué significa 'Ser el benjamín'?", "Ser el más alto", "Ser el hijo más joven", "Ser el más inteligente", "Ser el abuelo", "Ser el hijo más joven", "Se usa para referirse al menor de los hermanos."},
                {"¿Qué es un 'Nido vacío'?", "Una casa sin techo", "Un pájaro", "Cuando los hijos crecen y se van de casa", "Un niño viviendo solo", "Cuando los hijos crecen y se van de casa", "Se refiere a la soledad que sienten los padres cuando sus hijos se independizan."},
                {"¿Qué significa el modismo 'Llevar los pantalones'?", "Comprar ropa para todos", "Tener el mando o la autoridad en casa", "Ser diseñador", "Trabajar en una tienda", "Tener el mando o la autoridad en casa", "Se refiere a quien toma las decisiones importantes."},
                {"¿Qué es 'La cena de Nochebuena'?", "Una cena rápida", "La cena del 24 de diciembre con toda la familia", "Un desayuno", "Una fiesta de cumpleaños", "La cena del 24 de diciembre con toda la familia", "Es una de las reuniones familiares más importantes del año."},
                {"¿Qué significa 'Sacar adelante a la familia'?", "Ir de paseo", "Trabajar duro para proveer lo necesario", "Limpiar la casa", "Hacer ejercicio", "Trabajar duro para proveer lo necesario", "Se refiere al esfuerzo de los padres por el bienestar de sus hijos."},
                {"¿Qué es el 'Apellido'?", "El nombre de pila", "El nombre de la familia (heredado)", "Un apodo", "Una película", "El nombre de la familia (heredado)", "En los países hispanos se suelen usar dos apellidos (padre y madre)."},
                {"¿Qué se celebra en las 'Bodas de Oro'?", "10 años de casados", "50 años de casados", "El nacimiento de un bebé", "Un cumpleaños", "50 años de casados", "Plata son 25 años y Oro son 50 años."},
                {"¿Qué significa 'Tener mucha familia'?", "Tener una casa grande", "Tener muchos parientes (tíos, primos, etc.)", "Comer mucho", "Ser agricultor", "Tener muchos parientes (tíos, primos, etc.)", "Se refiere a una familia extendida muy numerosa."},
                {"¿Qué es el 'Día de la Madre/Padre'?", "Días para honrar a los progenitores", "Días de vacaciones escolares", "Elecciones", "Festivales religiosos", "Días para honrar a los progenitores", "Son fechas clave para reuniones familiares en todo el mundo hispano."},
                {"¿Qué significa 'Tratar de tú' o 'Tratar de usted'?", "Diferentes niveles de respeto/formalidad", "Hablar idiomas diferentes", "Gritar", "No hablar", "Diferentes niveles de respeto/formalidad", "En la familia, 'usted' se usa a veces con los abuelos por respeto."},
                {"¿Qué es un 'Mote' o 'Apodo' familiar?", "Un nombre legal", "Un nombre cariñoso o informal (ej. 'Pepe', 'Chino')", "Un apellido", "Un título noble", "Un nombre cariñoso o informal (ej. 'Pepe', 'Chino')", "Es muy común usar sobrenombres dentro de las familias hispanas."},
                {"¿Qué es 'La sobremesa'?", "Limpiar la mesa", "Conversar en la mesa después de comer", "Poner el mantel", "Dormir la siesta", "Conversar en la mesa después de comer", "Es una costumbre familiar muy arraigada donde se charla por horas tras la comida."},
                {"¿Qué significa 'Criar a los hijos'?", "Jugar con ellos", "Alimentar, educar y cuidar a los hijos hasta que crecen", "Llevarlos al parque", "Comprarles juguetes", "Alimentar, educar y cuidar a los hijos hasta que crecen", "Es el proceso completo de formación de un niño."}
        };
    }public static String[][] SpanishFamilyHard() {
        return new String[][] {
            {"Herencia y Genealogía", "Explorando relaciones complejas y términos legales familiares"},
            {"¿Qué es un 'Consanguíneo'?", "Un pariente por matrimonio", "Un pariente de sangre", "Un amigo", "Un vecino", "Un pariente de sangre", "Personas que tienen los mismos antepasados biológicos."},
            {"¿Quién es el 'Antepasado'?", "Un hijo futuro", "Un ascendiente (abuelo, bisabuelo, etc.)", "Un hermano", "Un vecino", "Un ascendiente (abuelo, bisabuelo, etc.)", "Cualquier persona de la que uno desciende."},
            {"¿Qué es la 'Genealogía'?", "El estudio de las plantas", "El estudio de la ascendencia y familia", "El estudio de la tierra", "Medicina", "El estudio de la ascendencia y familia", "La disciplina de rastrear el árbol familiar."},
            {"¿Qué significa 'Parentesco por afinidad'?", "Relación por sangre", "Relación creada por el matrimonio (política)", "Amistad", "Vecindad", "Relación creada por el matrimonio (política)", "Como los suegros o cuñados."},
            {"¿Qué es la 'Patria potestad'?", "Un país", "El conjunto de derechos y deberes de los padres sobre los hijos", "Una fiesta patria", "Un tipo de casa", "El conjunto de derechos y deberes de los padres sobre los hijos", "Es un término legal fundamental."},
            {"¿Qué es el 'Primogénito'?", "El hijo nacido primero", "El hijo más pequeño", "El hijo más alto", "El padre", "El hijo nacido primero", "Históricamente tenía derechos especiales de herencia."},
            {"¿Qué significa estar 'Distanciado' de la familia?", "Vivir en otra ciudad", "No tener relación ni hablarse por conflictos", "Estar de vacaciones", "Ser pariente lejano", "No tener relación ni hablarse por conflictos", "Ruptura de los lazos afectivos."},
            {"¿Qué es el 'Linaje'?", "Una línea de ropa", "La ascendencia o descendencia de una familia", "La riqueza", "La ubicación", "La ascendencia o descendencia de una familia", "Serie de antecesores y descendientes de una persona."},
            {"¿Quién es el 'Patriarca'?", "El hijo menor", "El varón que es cabeza de una familia o linaje", "Un abogado", "Un vecino", "El varón que es cabeza de una familia o linaje", "La figura masculina de mayor autoridad."},
            {"¿Qué es una 'Herencia'?", "Un regalo de cumpleaños", "Bienes y derechos que se reciben tras la muerte de alguien", "Un trabajo", "Un viaje", "Bienes y derechos que se reciben tras la muerte de alguien", "El patrimonio que pasa de padres a hijos."},
            {"¿Qué es el 'Árbol Genealógico'?", "Un árbol en el jardín", "La representación gráfica de los antepasados", "Un libro de cuentos", "Un mapa", "La representación gráfica de los antepasados", "Muestra las conexiones entre generaciones."},
            {"¿Qué es un 'Testamento'?", "Una historia familiar", "Documento legal donde se decide el destino de los bienes", "Una invitación", "Un acta de nacimiento", "Documento legal donde se decide el destino de los bienes", "Esencial para la transmisión del patrimonio familiar."},
            {"¿Qué es una 'Familia ensamblada'?", "Una familia que construye casas", "Familia formada por parejas con hijos de uniones anteriores", "Una familia con un solo hijo", "Familia que vive en el campo", "Familia formada por parejas con hijos de uniones anteriores", "Incluye padrastros, madrastras e hijastros."},
            {"¿Qué significa 'Hacerse cargo' de un familiar?", "Ignorarlo", "Asumir la responsabilidad de su cuidado", "Pelear con él", "Pedirle dinero", "Asumir la responsabilidad de su cuidado", "Frecuente con padres ancianos."},
            {"¿Qué es la 'Custodia' de los hijos?", "Una prisión", "El cuidado y vigilancia de los hijos tras un divorcio", "Un tipo de educación", "Una fiesta", "El cuidado y vigilancia de los hijos tras un divorcio", "Decidida legalmente por bienestar del menor."},
            {"¿Quién es un 'Tutor'?", "Un profesor", "Persona que cuida legalmente de un menor o incapacitado", "Un primo", "Un amigo", "Persona que cuida legalmente de un menor o incapacitado", "Asume el rol legal de protección."},
            {"¿Qué significa 'Heredar los rasgos'?", "Gastar dinero", "Tener las mismas características físicas que los padres", "Viajar mucho", "Estudiar", "Tener las mismas características físicas que los padres", "Se refiere a la genética familiar."},
            {"¿Qué es la 'Pensión alimenticia'?", "Un tipo de comida", "Pago legal para el sustento de los hijos tras una separación", "Un regalo", "Un ahorro", "Pago legal para el sustento de los hijos tras una separación", "Obligación financiera de un progenitor."},
            {"¿Qué significa 'Cuna' en sentido figurado?", "Una cama para bebés", "El origen familiar o social de una persona", "Una escuela", "Un hospital", "El origen familiar o social de una persona", "Ejemplo: 'Es de buena cuna' (viene de buena familia)."},
            {"¿Qué es una 'Tía abuela'?", "Tu tía favorita", "La hermana de tu abuelo o abuela", "La esposa de tu tío", "La madre de tu primo", "La hermana de tu abuelo o abuela", "Un nivel generacional superior a tus tíos."}
        };
    }
    private void seedForFamilySpanish(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = SpanishFamilyEasy();
        var md = SpanishFamilyMedium();
        var h = SpanishFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] FrenchFamilyEasy() {
        return new String[][] {
                {"Ma Généalogie", "Maîtriser les termes de parenté de base en français"},
                {"Comment appelles-tu le frère de ton père ?", "L'oncle", "Le neveu", "Le cousin", "Le beau-frère", "L'oncle", "Le frère de ton père ou de ta mère est ton oncle."},
                {"La fille de ta sœur est ta...", "Nièce", "Neveu", "Cousine", "Demi-sœur", "Nièce", "L'enfant féminin de ton frère ou de ta sœur est ta nièce."},
                {"Comment appelle-t-on une famille composée des parents et des enfants ?", "Famille élargie", "Famille nucléaire", "Famille d'accueil", "Famille monoparentale", "Famille nucléaire", "La famille nucléaire est l'unité de base : les parents et les enfants directs."},
                {"Quand un homme se marie, la mère de son épouse est sa...", "Belle-mère", "Marraine", "Tante", "Grand-mère", "Belle-mère", "En français, on ajoute 'beau-' ou 'belle-' pour la famille par alliance."},
                {"Comment appelles-tu ton enfant de sexe masculin ?", "Fille", "Fils", "Frère", "Neveu", "Fils", "Un fils est un garçon ; une fille est une enfant de sexe féminin."},
                {"Qui est ton 'cousin germain' ?", "L'enfant de ton oncle ou de ta tante", "L'enfant de ton frère", "Le frère de ton père", "Ton demi-frère", "L'enfant de ton oncle ou de ta tante", "Les cousins germains partagent les mêmes grands-parents."},
                {"Si ton père se remarie, sa nouvelle femme est ta...", "Tante", "Belle-mère", "Belle-mère (Madrâtre)", "Ex-mère", "Belle-mère (Madrâtre)", "Le terme 'belle-mère' désigne aussi bien la mère du conjoint que la nouvelle femme du père."},
                {"Comment appelles-tu la personne avec qui tu vas te marier ?", "Époux", "Fiancé / Fiancée", "Partenaire", "Parent", "Fiancé / Fiancée", "C'est la personne avec qui tu as un engagement formel de mariage."},
                {"Que sont les 'Frères et sœurs' ?", "Tes cousins", "Tes membres de la fratrie", "Tes jumeaux seulement", "Tes parents", "Tes membres de la fratrie", "En français, on utilise aussi le mot 'fratrie' pour désigner l'ensemble des frères et sœurs."},
                {"Comment appelles-tu le père de ton père ?", "Arrière-grand-père", "Grand-père (Papy)", "Oncle", "Beau-père", "Grand-père (Papy)", "Le grand-père est le père de l'un de tes parents."},
                {"Quel est le féminin de 'Gendre' (le mari de ta fille) ?", "Gendrette", "Bru / Belle-fille", "Tante", "Fille", "Bru / Belle-fille", "Gendre est pour l'homme, bru ou belle-fille est pour la femme."},
                {"Qu'est-ce qu'un 'Enfant unique' ?", "Un enfant sans frères ni sœurs", "L'aîné", "Le cadet", "Un enfant avec un jumeau", "Un enfant sans frères ni sœurs", "Un enfant qui n'a ni frère ni sœur."},
                {"Qui est ton 'Conjoint' ?", "Ton frère", "Ton mari ou ta femme", "Ton père", "Ton voisin", "Ton mari ou ta femme", "Conjoint est un terme formel pour l'époux ou l'épouse."},
                {"Comment appelles-tu le fils de ton fils ?", "Neveu", "Arrière-petit-fils", "Petit-fils", "Cousin", "Petit-fils", "Le fils de ton fils ou de ta fille est ton petit-fils."},
                {"Qu'est-ce qu'un 'Demi-frère' ?", "Le fils de ton beau-père ou de ta belle-mère", "Ton frère biologique", "Ton beau-frère", "Ton cousin", "Le fils de ton beau-père ou de ta belle-mère", "Ils partagent un seul parent biologique ou sont liés par remariage."},
                {"Comment appelles-tu la fille de ton frère ou de ta sœur ?", "Nièce", "Neveu", "Cousine", "Petite-fille", "Nièce", "La nièce est la fille d'un frère ou d'une sœur."},
                {"Qu'est-ce qu'un 'Orphelin' ?", "Un enfant unique", "Un enfant qui a perdu ses parents", "Un enfant adopté", "Un enfant turbulent", "Un enfant qui a perdu ses parents", "C'est un enfant dont le père et la mère sont décédés."},
                {"Qu'est-ce qu'une 'Veuve' ?", "Une femme dont le mari est mort", "Une femme divorcée", "Une femme célibataire", "Une mère de jumeaux", "Une femme dont le mari est mort", "L'homme dont l'épouse est morte est un veuf."},
                {"Comment appelles-tu le frère de ton épouse ?", "Beau-frère", "Beau-père", "Oncle", "Cousin", "Beau-frère", "C'est ton beau-frère par alliance."},
                {"Comment appelle-t-on les parents de tes parents ?", "Grands-parents", "Arrière-parents", "Beaux-parents", "Fratrie", "Grands-parents", "Ce sont Papy et Mamie ensemble."}
        };
    }public static String[][] FrenchFamilyMedium() {
        return new String[][] {
                {"Vie et Traditions Familiales", "Comprendre les expressions et les coutumes sociales françaises"},
                {"Que signifie l'expression 'Tel père, tel fils' ?", "Les enfants sont différents des parents", "Le fils ressemble à son père", "Le père est gentil", "La famille est riche", "Le fils ressemble à son père", "Cela signifie que les enfants héritent souvent des traits de leurs parents."},
                {"En France, qu'est-ce qu'un 'Parrain' ou une 'Marraine' ?", "Un voisin", "Un protecteur choisi pour le baptême", "Un médecin", "Le frère aîné", "Un protecteur choisi pour le baptême", "Ils ont un rôle moral important envers l'enfant."},
                {"Que signifie être le 'Petit dernier' de la famille ?", "L'enfant le plus grand", "L'enfant le plus jeune", "L'enfant le plus intelligent", "Le grand-père", "L'enfant le plus jeune", "C'est l'enfant né en dernier dans la fratrie (le benjamin)."},
                {"Que signifie l'expression 'Tomber dans les pommes' ?", "Aimer les fruits", "S'évanouir", "Se disputer en famille", "Manger beaucoup", "S'évanouir", "C'est une expression courante, bien que non spécifique à la famille."},
                {"Que fête-t-on le 24 décembre au soir en famille en France ?", "Pâques", "Le Réveillon de Noël", "Le 14 juillet", "La Chandeleur", "Le Réveillon de Noël", "C'est le repas familial le plus important de l'année."},
                {"Que signifie 'Avoir un air de famille' ?", "Être musicien", "Se ressembler physiquement", "Être en colère", "Habiter loin", "Se ressembler physiquement", "Avoir des traits physiques communs avec ses parents."},
                {"Qu'est-ce qu'une 'Famille recomposée' ?", "Une famille très riche", "Un couple avec des enfants nés d'unions précédentes", "Une famille avec des jumeaux", "Une famille qui voyage", "Un couple avec des enfants nés d'unions précédentes", "C'est un modèle familial très courant aujourd'hui."},
                {"Que signifie 'Être le mouton noir' de la famille ?", "L'enfant préféré", "Celui qui est différent ou mal vu", "Le plus âgé", "Le plus riche", "Celui qui est différent ou mal vu", "C'est la personne qui ne suit pas les règles de la famille."},
                {"Que signifie l'expression 'C'est le portrait tout craché de son père' ?", "Il déteste son père", "Il ressemble exactement à son père", "Il est plus grand que son père", "Il ne connaît pas son père", "Il ressemble exactement à son père", "Cela souligne une ressemblance frappante."},
                {"Qu'est-ce que 'Le livret de famille' ?", "Un livre de contes", "Un document officiel remis lors du mariage ou d'une naissance", "Un album photo", "Un agenda", "Un document officiel remis lors du mariage ou d'une naissance", "C'est un document administratif essentiel en France."},
                {"Que fête-t-on lors des 'Noces d'Or' ?", "10 ans de mariage", "50 ans de mariage", "25 ans de mariage", "La naissance d'un bébé", "50 ans de mariage", "L'argent est pour 25 ans, l'or pour 50 ans."},
                {"Que signifie 'Laver son linge sale en famille' ?", "Faire la lessive ensemble", "Régler ses disputes en privé", "Acheter une machine à laver", "Avoir beaucoup d'enfants", "Régler ses disputes en privé", "Cela signifie qu'il ne faut pas parler des problèmes familiaux en public."},
                {"En France, comment appelle-t-on familièrement la grand-mère ?", "Mamie", "Nounou", "Tata", "Maman", "Mamie", "Papy et Mamie sont les noms affectueux les plus utilisés."},
                {"Qu'est-ce qu'une 'Mère au foyer' ?", "Une femme qui construit des maisons", "Une femme qui s'occupe de sa maison et de ses enfants", "Une architecte", "Une voyageuse", "Une femme qui s'occupe de sa maison et de ses enfants", "Elle se consacre à l'éducation de ses enfants à plein temps."},
                {"Que signifie 'Faire la tête' ?", "Être intelligent", "Bouder ou être mécontent", "Se laver les cheveux", "Avoir mal à la tête", "Bouder ou être mécontent", "C'est une expression souvent entendue lors de disputes familiales."},
                {"Quelle est la tradition de la 'Chandeleur' en famille ?", "Manger des crêpes", "Manger du chocolat", "Offrir des fleurs", "Aller à la plage", "Manger des crêpes", "C'est une fête familiale gourmande en février."},
                {"Que signifie 'Passer du temps avec ses proches' ?", "Travailler", "Passer du temps avec sa famille et ses amis intimes", "Dormir", "Faire les courses", "Passer du temps avec sa famille et ses amis intimes", "Les proches incluent la famille et les amis très chers."},
                {"Qu'est-ce qu'un 'Surnom' ?", "Un nom de famille", "Un nom affectueux ou informel (ex: Lolo, Mimi)", "Une adresse", "Un diplôme", "Un nom affectueux ou informel (ex: Lolo, Mimi)", "C'est très courant dans les familles françaises."},
                {"Que signifie 'Prendre soin des siens' ?", "Être égoïste", "S'occuper de sa famille", "Partir en vacances seul", "Vendre sa maison", "S'occuper de sa famille", "Cela désigne le dévouement envers ses parents và con cái."},
                {"Comment appelle-t-on le repas du dimanche midi en France ?", "Le brunch", "Le déjeuner dominical", "Le goûter", "Le souper", "Le déjeuner dominical", "C'est un moment traditionnel de réunion familiale."}
        };
    }public static String[][] FrenchFamilyHard() {
        return new String[][] {
                {"Héritage et Généalogie", "Explorer les relations complexes et les termes juridiques familiaux"},
                {"Qu'est-ce que la 'Consanguinité' ?", "L'alliance par mariage", "Le lien de parenté par le sang", "L'amitié", "Le voisinage", "Le lien de parenté par le sang", "Cela désigne des personnes ayant des ancêtres biologiques communs."},
                {"Qui est un 'Ancêtre' ?", "Un enfant futur", "Un ascendant lointain (arrière-grand-parent, etc.)", "Un frère", "Un voisin", "Un ascendant lointain (arrière-grand-parent, etc.)", "Toute personne de qui l'on descend."},
                {"Qu'est-ce que la 'Généalogie' ?", "L'étude des plantes", "L'étude de l'ascendance et des familles", "L'étude de la terre", "La médecine", "L'étude de l'ascendance et des familles", "La discipline qui permet de construire son arbre généalogique."},
                {"Qu'est-ce que l'autorité parentale ?", "Le pouvoir du plus riche", "L'ensemble des droits et devoirs des parents envers l'enfant", "Une fête", "Une punition", "L'ensemble des droits et devoirs des parents envers l'enfant", "C'est un concept juridique majeur pour la protection du mineur."},
                {"Qu'est-ce qu'un 'Héritier' ?", "Celui qui travaille le plus", "La personne qui reçoit les biens après un décès", "Le fils aîné seulement", "Un ami", "La personne qui reçoit les biens after un décès", "C'est la personne qui succède au défunt."},
                {"Qu'est-ce qu'un 'Testament' ?", "Une histoire de famille", "Un document où l'on décide de la répartition de ses biens après la mort", "Une invitation", "Un acte de naissance", "Un document où l'on décide de la répartition de ses biens sau la mort", "C'est essentiel pour organiser sa succession."},
                {"Que signifie 'Être en froid' avec un membre de sa famille ?", "Avoir froid", "Être en conflit et ne plus se parler", "Partir au ski", "Être un parent éloigné", "Être en conflit et ne plus se parler", "C'est une rupture temporaire hoặc lâu dài của quan hệ."},
                {"Qu'est-ce que la 'Filiation' ?", "Une collection de fils", "Le lien juridique unissant un enfant à son père ou sa mère", "Une entreprise", "Une promenade", "Le lien juridique unissant un enfant à son père hoặc sa mẹ", "Elle peut être biologique ou adoptive."},
                {"Qui est le 'Patriarche' ?", "Le fils cadet", "L'homme le plus âgé ou le plus respecté d'une famille", "Un avocat", "Un enfant", "L'homme le plus âgé ou le plus respecté d'une famille", "C'est souvent la figure d'autorité masculine d'un clan."},
                {"Qu'est-ce qu'une 'Succession' ?", "Une réussite", "La transmission des biens d'une personne décédée", "Un voyage", "Un mariage", "La transmission des biens d'une personne décédée", "C'est le processus juridique après un décès."},
                {"Qu'est-ce que l'arbre 'Ascendant' ?", "Les enfants và petits-enfants", "Les parents, grands-parents và ancêtres", "Les frères và sœurs", "Les cousins", "Les parents, grands-parents và ancêtres", "Cela remonte le temps vers les racines."},
                {"Qu'est-ce que l'acte de naissance ?", "Une lettre", "Un document officiel prouvant la naissance và la parenté", "Un livre", "Une photo", "Un document officiel prouvant la naissance và la parenté", "C'est le premier acte civil de la vie."},
                {"Que signifie le terme 'Pupille de l'État' ?", "Un élève brillant", "Un enfant sans famille pris en charge par l'État", "Un professeur", "Un policier", "Un enfant sans gia đình được chăm sóc bởi Nhà nước", "Ce sont des enfants protégés par la collectivité."},
                {"Qu'est-ce que la 'Réserve héréditaire' ?", "Une forêt", "La part des biens obligatoirement réservée aux enfants", "Une banque", "Un jouet", "La part des biens obligatoirement réservée aux enfants", "En droit français, on không thể truất quyền thừa kế hoàn toàn của con cái."},
                {"Qu'est-ce que la 'Garde alternée' ?", "Une prison", "Le partage du temps de l'enfant entre ses deux parents séparés", "Une école", "Une fête", "Le partage du temps de l'enfant entre ses deux parents séparés", "L'enfant vit tour à tour chez son père và sa mẹ."},
                {"Qui est un 'Tuteur' ?", "Un jardinier", "Une personne chargée de protéger un mineur hoặc un majeur incapable", "Un cousin", "Un ami", "Une người chịu trách nhiệm bảo vệ trẻ vị thành niên hoặc người mất năng lực", "Il agit au nom de la personne protégée."},
                {"Que signifie 'Avoir des ancêtres communs' ?", "Vivre dans la même rue", "Partager la même lignée génétique", "Avoir les mêmes vêtements", "Travailler ensemble", "Partager la même lignée génétique", "C'est le fondement de la parenté."},
                {"Qu'est-ce qu'une 'Pension alimentaire' ?", "Une sorte de nourriture", "Une somme d'argent versée pour l'entretien d'un enfant sau une séparation", "Un cadeau", "Une épargne", "Một khoản tiền trả để nuôi dưỡng con cái sau khi ly hôn", "C'est une obligation légale."},
                {"Que signifie l'expression 'De bonne famille' ?", "Une famille qui cuisine bien", "Une famille respectable ou aisée", "Une famille nombreuse", "Une famille avec des médecins", "Une gia đình đáng kính hoặc giàu có", "Cela désigne souvent une origine sociale privilégiée."},
                {"Qu'est-ce qu'une 'Grand-tante' ?", "Ta tante préférée", "La sœur de ton grand-père ou de ta grand-mère", "La femme de ton oncle", "La mère de ton cousin", "La sœur de ton grand-père hoặc de ta bà nội/ngoại", "C'est le niveau au-dessus de tes tantes habituelles."}
        };
    }
    private void seedForFamilyFrench(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = FrenchFamilyEasy();
        var md = FrenchFamilyMedium();
        var h = FrenchFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] GermanFamilyEasy() {
        return new String[][] {
                {"Meine Familie", "Grundlegende Begriffe und Verwandtschaftsverhältnisse auf Deutsch"},
                {"Wie nennt man den Bruder der Mutter?", "Der Onkel", "Der Neffe", "Der Cousin", "Der Schwager", "Der Onkel", "Der Bruder deiner Mutter oder deines Vaters ist dein Onkel."},
                {"Die Tochter deiner Schwester ist deine...", "Nichte", "Neffe", "Cousine", "Stiefschwester", "Nichte", "Die weibliche Nachkommin eines Geschwisters ist die Nichte."},
                {"Wie nennt man eine Familie, die nur aus Eltern und Kindern besteht?", "Großfamilie", "Kernfamilie", "Patchworkfamilie", "Einzelhaushalt", "Kernfamilie", "Die Kernfamilie ist die kleinste soziale Einheit."},
                {"Wie nennt man die Mutter des Ehepartners?", "Die Stiefmutter", "Die Schwiegermutter", "Die Tante", "Die Oma", "Die Schwiegermutter", "Alle Verwandten durch Heirat erhalten die Vorsilbe 'Schwieger-'."},
                {"Wie nennt man ein männliches Kind?", "Die Tochter", "Der Sohn", "Der Bruder", "Der Neffe", "Der Sohn", "Ein männliches Kind ist der Sohn, ein weibliches die Tochter."},
                {"Wer ist dein 'Cousin'?", "Das Kind von Onkel oder Tante", "Das Kind deines Bruders", "Der Bruder deines Vaters", "Dein Stiefbruder", "Das Kind von Onkel oder Tante", "Cousins sind die Kinder der Geschwister deiner Eltern."},
                {"Wenn dein Vater erneut heiratet, ist seine neue Frau deine...", "Tante", "Schwiegermutter", "Stiefmutter", "Ex-Mutter", "Stiefmutter", "'Stief-' bezeichnet Verwandtschaft durch eine neue Ehe der Eltern."},
                {"Wie nennt man die Person, mit der man verlobt ist?", "Ehepartner", "Verlobter / Verlobte", "Partner", "Verwandter", "Verlobter / Verlobte", "Nach dem Heiratsantrag ist man bis zur Hochzeit verlobt."},
                {"Was sind 'Geschwister'?", "Cousins", "Brüder und Schwestern", "Zwillinge", "Eltern", "Brüder und Schwestern", "Geschwister ist der Sammelbegriff für Bruder und Schwester."},
                {"Wie nennt man den Vater des Vaters?", "Urgroßvater", "Großvater (Opa)", "Onkel", "Stiefvater", "Großvater (Opa)", "Der Großvater ist der Vater eines Elternteils."},
                {"Wie nennt man den Ehemann der Tochter?", "Schwiegersohn", "Schwager", "Neffe", "Cousin", "Schwiegersohn", "Der Mann der Tochter ist der Schwiegersohn."},
                {"Was ist ein 'Einzelkind'?", "Ein Kind ohne Geschwister", "Das älteste Kind", "Das jüngste Kind", "Ein Kind mit einem Zwilling", "Ein Kind ohne Geschwister", "Jemand, der keine Brüder oder Schwestern hat."},
                {"Wer ist der 'Ehegatte'?", "Der Bruder", "Der Ehemann oder die Ehefrau", "Der Vater", "Der Nachbar", "Der Ehemann oder die Ehefrau", "Ehegatte ist ein formaler Begriff für den Partner in einer Ehe."},
                {"Wie nennt man das Kind des Sohnes?", "Neffe", "Urenkel", "Enkel", "Cousin", "Enkel", "Das Kind der eigenen Kinder ist das Enkelkind."},
                {"Was ist ein 'Halbbruder'?", "Ein Freund", "Ein Bruder mit einem gemeinsamen Elternteil", "Der Schwager", "Der Cousin", "Ein Bruder mit einem gemeinsamen Elternteil", "Halbgeschwister haben entweder dieselbe Mutter oder denselben Vater."},
                {"Was ist der 'Familienname'?", "Der Vorname", "Der Nachname", "Der Spitzname", "Der Geburtsort", "Der Nachname", "Der Familienname wird meist vom Vater oder der Mutter übernommen."},
                {"Wer gehört zur 'Verwandtschaft'?", "Nur die Eltern", "Alle biologisch oder rechtlich verbundenen Personen", "Nur Freunde", "Nachbarn", "Alle biologisch oder rechtlich verbundenen Personen", "Dazu gehören Tanten, Onkel, Cousins, Großeltern usw."},
                {"Was ist eine 'Witwe'?", "Eine Frau, deren Ehemann gestorben ist", "Eine geschiedene Frau", "Eine ledige Frau", "Eine Mutter von Zwillingen", "Eine Frau, deren Ehemann gestorben ist", "Ein Mann in dieser Situation heißt Witwer."},
                {"Wie nennt man den Bruder der Ehefrau?", "Schwager", "Schwiegervater", "Onkel", "Cousin", "Schwager", "Der Bruder des Partners ist der Schwager."},
                {"Wie nennt man die Eltern der Eltern zusammen?", "Großeltern", "Ureltern", "Schwiegereltern", "Geschwister", "Großeltern", "Oma und Opa zusammen nennt man Großeltern."}
        };
    }public static String[][] GermanFamilyMedium() {
        return new String[][] {
                {"Familienleben & Traditionen", "Redewendungen und soziale Bräuche in deutschsprachigen Ländern"},
                {"Was bedeutet die Redewendung 'Der Apfel fällt nicht weit vom Stamm'?", "Kinder sind anders als die Eltern", "Kinder sind den Eltern sehr ähnlich", "Äpfel sind gesund", "Die Familie lebt im Wald", "Kinder sind den Eltern sehr ähnlich", "Das bedeutet, dass Kinder oft ähnliche Charakterzüge wie ihre Eltern haben."},
                {"Was ist eine 'Patchworkfamilie'?", "Eine Familie, die näht", "Eine Familie mit Kindern aus verschiedenen Ehen", "Eine sehr reiche Familie", "Eine Familie ohne Kinder", "Eine Familie mit Kindern aus verschiedenen Ehen", "In Deutschland ist dieses Modell heute sehr verbreitet."},
                {"Was bedeutet es, das 'schwarze Schaf' der Familie zu sein?", "Das Lieblingskind", "Jemand, der aus dem Rahmen fällt oder Schande bringt", "Das älteste Mitglied", "Das reichste Mitglied", "Jemand, der aus dem Rahmen fällt oder Schande bringt", "Jemand, der sich anders verhält als der Rest der Familie."},
                {"Wer ist das 'Nesthäkchen'?", "Der Vogel im Garten", "Das jüngste Kind der Familie", "Das älteste Kind", "Der Familienvater", "Das jüngste Kind der Familie", "Das jüngste Kind wird oft besonders verwöhnt."},
                {"Was feiert man in Deutschland traditionell am 24. Dezember?", "Ostern", "Heiligabend (Weihnachten)", "Nikolaus", "Pfingsten", "Heiligabend (Weihnachten)", "Dies ist das wichtigste Familienfest im Jahr."},
                {"Was bedeutet 'Blut ist dicker als Wasser'?", "Wasser ist wichtiger", "Die Familie hält in der Not zusammen", "Blut ist schwer zu reinigen", "Freunde sind wichtiger als Familie", "Die Familie hält in der Not zusammen", "Es bedeutet, dass die Bindung zur Familie stärker ist als jede andere."},
                {"Was ist ein 'Pate' oder eine 'Patente'?", "Ein Feind", "Ein spiritueller Begleiter für ein Kind", "Ein Arzt", "Ein Geschwisterkind", "Ein spiritueller Begleiter für ein Kind", "Paten übernehmen bei der Taufe eine besondere Verantwortung für das Kind."},
                {"Was bedeutet die Redewendung 'Wie der Vater, so der Sohn'?", "Der Vater mag den Sohn", "Der Sohn ist dem Vater sehr ähnlich", "Der Vater ist größer", "Der Sohn ist älter", "Der Sohn ist dem Vater sehr ähnlich", "Man nutzt dies, um Ähnlichkeiten im Verhalten festzustellen."},
                {"Was ist ein 'Stammbaum'?", "Ein großer Baum im Garten", "Eine grafische Darstellung der Vorfahren", "Ein Buch mit Märchen", "Ein Wanderstock", "Eine grafische Darstellung der Vorfahren", "Er zeigt die Verbindung zwischen den Generationen."},
                {"Was bedeutet es, 'die Hosen an zu haben'?", "Kleidung kaufen", "Die Entscheidungen in der Familie treffen", "Mode zu machen", "Im Geschäft zu arbeiten", "Die Entscheidungen in der Familie treffen", "Es bezeichnet die Person, die die Kontrolle im Haushalt hat."},
                {"Was ist eine 'Hausfrau' oder ein 'Hausmann'?", "Jemand, der Häuser baut", "Jemand, der sich um Haushalt und Kinder kümmert", "Ein Immobilienmakler", "Ein Gast in einem Hotel", "Jemand, der sich um Haushalt und Kinder kümmert", "Diese Person übernimmt die Familienarbeit zu Hause."},
                {"Was bedeutet 'In jemandes Fußstapfen treten'?", "Hinter jemandem hergehen", "Derselben Beruf oder Lebensweg wie ein Familienmitglied wählen", "Das Haus putzen", "Wandern gehen", "Derselben Beruf oder Lebensweg wie ein Familienmitglied wählen", "Oft wenn Kinder denselben Beruf wie die Eltern ergreifen."},
                {"Was ist 'Verwandtschaft ersten Grades'?", "Entfernte Verwandte", "Direkte Verwandte wie Eltern oder Kinder", "Freunde", "Nachbarn", "Direkte Verwandte wie Eltern oder Kinder", "Dies ist ein rechtlicher Begriff für die engsten Angehörigen."},
                {"Was feiert man bei einer 'Goldenen Hochzeit'?", "10 Jahre Ehe", "50 Jahre Ehe", "25 Jahre Ehe", "Den Hausbau", "50 Jahre Ehe", "25 Jahre ist Silber, 50 Jahre ist Gold."},
                {"Was bedeutet es, 'jemanden unter die Fittiche nehmen'?", "Vögel jagen", "Jemanden beschützen und anleiten", "Jemanden ignorieren", "Viel Geld ausgeben", "Jemanden beschützen und anleiten", "Ein älteres Familienmitglied kümmert sich um ein jüngeres."},
                {"Was ist ein 'Generationenkonflikt'?", "Ein Loch im Boden", "Unstimmigkeiten zwischen Jung und Alt", "Ein Familienfoto", "Ein Streit zwischen Nachbarn", "Unstimmigkeiten zwischen Jung und Alt", "Unterschiedliche Ansichten aufgrund des Altersunterschieds."},
                {"Was ist 'elternzeit'?", "Urlaub für die Eltern", "Zeitliche Freistellung vom Beruf nach der Geburt eines Kindes", "Die Schulzeit der Kinder", "Ein Fest für Senioren", "Zeitliche Freistellung vom Beruf nach der Geburt eines Kindes", "In Deutschland haben Väter und Mütter Anspruch darauf."},
                {"Was ist ein 'Kosename'?", "Ein Name für einen Hund", "Ein liebevoller Spitzname innerhalb der Familie", "Ein Nachname", "Ein Titel", "Ein liebevoller Spitzname innerhalb der Familie", "Beispiele sind 'Schatz', 'Hasi' oder 'Mausi'."},
                {"Was sind 'Ahnen'?", "Die Kinder der Zukunft", "Die Vorfahren einer Familie", "Die Haustiere", "Die Nachbarn", "Die Vorfahren einer Familie", "Personen, von denen man abstammt (Großeltern, Urgroßeltern usw.)."},
                {"Was bedeutet 'auf großem Fuß leben'?", "Große Schuhe haben", "Viel Geld für die Familie ausgeben", "Viel Platz brauchen", "Sport treiben", "Viel Geld für die Familie ausgeben", "Es bedeutet, einen luxuriösen Lebensstil zu führen."}
        };
    }public static String[][] GermanFamilyHard() {
        return new String[][] {
                {"Erbe & Ahnenforschung", "Komplexe Beziehungen und rechtliche Begriffe im Familienkontext"},
                {"Was bedeutet 'blutsverwandt'?", "Durch Heirat verbunden", "Biologisch miteinander verwandt", "Befreundet", "Nachbarn", "Biologisch miteinander verwandt", "Personen, die dieselben genetischen Vorfahren haben."},
                {"Was ist ein 'Stammhalter'?", "Ein Baum im Wald", "Traditionell der erste Sohn, der den Familiennamen weiterführt", "Ein Erbe", "Ein Berater", "Traditionell der erste Sohn, der den Familiennamen weiterführt", "Ein heute eher veralteter Begriff für den erstgeborenen Sohn."},
                {"Was ist 'Genealogie'?", "Die Lehre von Pflanzen", "Die Erforschung der Familiengeschichte", "Die Lehre von der Erde", "Medizin", "Die Erforschung der Familiengeschichte", "Die systematische Suche nach Vorfahren."},
                {"Was ist das 'Sorgerecht'?", "Ein Land", "Das Recht und die Pflicht, für ein minderjähriges Kind zu sorgen", "Ein Fest", "Eine Versicherung", "Das Recht und die Pflicht, für ein minderjähriges Kind zu sorgen", "Ein zentraler Begriff im deutschen Familienrecht."},
                {"Was ist ein 'Adoptivkind'?", "Ein Kind, das weggegeben wird", "Ein Kind, das rechtlich als eigenes Kind angenommen wurde", "Ein biologisches Kind", "Ein Gastkind", "Ein Kind, das rechtlich als eigenes Kind angenommen wurde", "Adoption schafft ein rechtliches Familienverhältnis."},
                {"Was ist das 'Erbe'?", "Ein Geburtstagsgeschenk", "Der Nachlass einer verstorbenen Person", "Ein Beruf", "Ein Ausflug", "Der Nachlass einer verstorbenen Person", "Das Vermögen, das von Generation zu Generation weitergegeben wird."},
                {"Was bedeutet 'entfremdet' sein?", "In einer anderen Stadt wohnen", "Keinen Kontakt mehr zur Familie haben aufgrund von Konflikten", "Urlaub machen", "Ein entfernter Verwandter sein", "Keinen Kontakt mehr zur Familie haben aufgrund von Konflikten", "Wenn die familiäre Bindung zerbrochen ist."},
                {"Was ist eine 'Erbfolge'?", "Eine Modenschau", "Die gesetzliche Reihenfolge der Erben", "Reichtum", "Ein Ort", "Die gesetzliche Reihenfolge der Erben", "Regelt, wer nach dem Tod eines Angehörigen dessen Besitz erhält."},
                {"Wer ist das 'Oberhaupt' der Familie?", "Das jüngste Kind", "Die Person mit der höchsten Autorität", "Ein Anwalt", "Ein Nachbar", "Die Person mit der höchsten Autorität", "Früher meist der Vater, heute oft gemeinschaftlich."},
                {"Was ist ein 'Testament'?", "Eine Familiengeschichte", "Ein Dokument, das den letzten Willen bezüglich des Erbes regelt", "Eine Einladung", "Eine Geburtsurkunde", "Ein Dokument, das den letzten Willen bezüglich des Erbes regelt", "Wichtig für die Regelung des Familienbesitzes."},
                {"Was ist eine 'Vormundschaft'?", "Ein Lehrer", "Die rechtliche Fürsorge für eine minderjährige Person durch Dritte", "Ein Cousin", "Ein Freund", "Die rechtliche Fürsorge für eine minderjährige Person durch Dritte", "Wird gerichtlich festgelegt, wenn Eltern nicht sorgen können."},
                {"Was ist der 'Pflichtteil'?", "Eine Hausaufgabe", "Der gesetzlich garantierte Mindestanteil am Erbe für nahe Angehörige", "Ein Geschenk", "Eine Ersparnis", "Der gesetzlich garantierte Mindestanteil am Erbe für nahe Angehörige", "Ein wichtiger Begriff im deutschen Erbrecht."},
                {"Was bedeutet 'personenstand'?", "Wie man steht", "Der Familienstand (ledig, verheiratet, geschieden, verwitwet)", "Die Körpergröße", "Das Alter", "Der Familienstand (ledig, verheiratet, geschieden, verwitwet)", "Wird in offiziellen Dokumenten abgefragt."},
                {"Was ist ein 'Urahn'?", "Ein Enkel", "Ein sehr weit zurückliegender Vorfahre", "Ein Freund", "Ein Nachbar", "Ein sehr weit zurückliegender Vorfahre", "Der Ursprung einer langen Ahnenreihe."},
                {"Was ist 'Unterhalt'?", "Ein Gespräch", "Finanzielle Unterstützung für Kinder oder Ex-Partner nach einer Trennung", "Ein Hobby", "Ein Geschenk", "Finanzielle Unterstützung für Kinder oder Ex-Partner nach einer Trennung", "Dient der Sicherung des Lebensbedarfs."},
                {"Was bedeutet 'bürgerliches Gesetzbuch' (BGB) im Familienkontext?", "Ein Kochbuch", "Das Gesetzbuch, das das Familienrecht in Deutschland regelt", "Eine Reisebeschreibung", "Ein Märchenbuch", "Das Gesetzbuch, das das Familienrecht in Deutschland regelt", "Das wichtigste Gesetz für Ehe und Familie."},
                {"Was ist eine 'Großtante'?", "Deine Lieblingstante", "Die Schwester deines Großvaters oder deiner Großmutter", "Die Frau deines Onkels", "Die Mutter deines Cousins", "Die Schwester deines Großvaters oder deiner Großmutter", "Eine Generation über deinen normalen Tanten."},
                {"Was ist ein 'Ehevertrag'?", "Ein Brief", "Ein rechtliches Dokument zur Regelung finanzieller Dinge in der Ehe", "Ein Foto", "Eine Einladung", "Ein rechtliches Dokument zur Regelung finanzieller Dinge in der Ehe", "Wird oft vor der Hochzeit geschlossen."},
                {"Was bedeutet 'Abstammung'?", "Das Alter", "Die Herkunft von bestimmten Vorfahren", "Der Beruf", "Ein Hobby", "Die Herkunft von bestimmten Vorfahren", "Die biologische Linie einer Person."},
                {"Was ist die 'Geburtsurkunde'?", "Ein Schulzeugnis", "Das offizielle Dokument über die Geburt und die Eltern", "Ein Ausweis", "Ein Brief", "Das offizielle Dokument über die Geburt und die Eltern", "Das erste wichtige Dokument im Leben eines Menschen."}
        };
    }
    private void seedForFamilyGerman(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = GermanFamilyEasy();
        var md = GermanFamilyMedium();
        var h = GermanFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ChineseFamilyEasy() {
        return new String[][] {
                {"我的家谱", "掌握基础亲属称呼与核心家庭成员"},
                {"爸爸的爸爸应该称呼什么？", "爷爷", "外公", "伯伯", "叔叔", "爷爷", "在汉语中，爸爸的父亲是爷爷，妈妈的父亲是外公。"},
                {"妈妈的妈妈应该称呼什么？", "奶奶", "外婆", "阿姨", "姑姑", "外婆", "妈妈的母亲通常称为外婆或劳劳。"},
                {"爸爸的哥哥被称为？", "叔叔", "伯伯", "舅舅", "大爷", "伯伯", "爸爸的哥哥是伯伯，弟弟是叔叔。"},
                {"妈妈的兄弟（无论大小）统一称呼为？", "叔叔", "伯伯", "舅舅", "姑父", "舅舅", "妈妈方的兄弟不论年龄大小都叫舅舅。"},
                {"你的亲生兄弟姐妹被称为？", "堂兄弟", "表兄弟", "同胞兄弟姐妹", "亲戚", "同胞兄弟姐妹", "指同一个父母所生的孩子。"},
                {"爸爸的姐妹被称为？", "阿姨", "姑姑", "舅妈", "姨妈", "姑姑", "爸爸的姐妹是姑姑，妈妈的姐妹是姨妈。"},
                {"妈妈的姐妹被称为？", "阿姨/姨妈", "姑姑", "舅妈", "奶奶", "阿姨/姨妈", "妈妈的姐妹称为姨妈或阿姨。"},
                {"你哥哥的女儿是你的？", "侄女", "外甥女", "孙女", "妹妹", "侄女", "兄弟的孩子是侄子/侄女，姐妹的孩子是外甥/外甥女。"},
                {"过年时，晚辈向长辈拜年，长辈给的钱叫什么？", "工资", "小费", "压岁钱 / 红包", "奖金", "压岁钱 / 红包", "这是中国春节的重要传统，寓意岁岁平安。"},
                {"“儿媳妇”是指谁？", "儿子的妻子", "女儿的丈夫", "哥哥的妻子", "姐姐的丈夫", "儿子的妻子", "儿子的妻子叫儿媳，女儿的丈夫叫女婿。"},
                {"如果你没有兄弟姐妹，你被称为？", "独生子女", "老大", "小弟", "孤儿", "独生子女", "指家里唯一的孩子。"},
                {"爸爸的弟弟的妻子，你应该叫她？", "伯母", "婶婶", "舅妈", "姨妈", "婶婶", "叔叔的妻子称为婶婶。"},
                {"妈妈的兄弟的妻子，你应该叫她？", "伯母", "婶婶", "舅妈", "姨妈", "舅妈", "舅舅的妻子称为舅妈。"},
                {"家里的第一代、第二代、第三代人聚在一起，这叫？", "三代同堂", "四分五裂", "独立自主", "外亲", "三代同堂", "指祖孙三代共同居住或聚在一起。"},
                {"“亲家”是指什么关系？", "好朋友", "两家儿女联姻后的父母关系", "远房亲戚", "邻居", "两家儿女联姻后的父母关系", "儿媳和女婿的父母互称为亲家。"},
                {"你的亲姐妹中，年龄比你大的是？", "妹妹", "姐姐", "表姐", "堂姐", "姐姐", "年龄大的女性同胞是姐姐。"},
                {"你的亲兄弟中，年龄比你小的是？", "哥哥", "弟弟", "表弟", "堂弟", "弟弟", "年龄小的男性同胞是弟弟。"},
                {"爸爸的姐妹的丈夫，你应该叫他？", "姑父", "姨父", "舅父", "叔叔", "姑父", "姑姑的丈夫是姑父。"},
                {"妈妈的姐妹的丈夫，你应该叫他？", "姑父", "姨父", "舅父", "叔叔", "姨父", "姨妈的丈夫是姨父。"},
                {"“曾祖父”是指哪一位？", "爷爷的爸爸", "爸爸的爸爸", "妈妈的爸爸", "哥哥", "爷爷的爸爸", "也称为太爷爷，是祖父的父亲。"}
        };
    }public static String[][] ChineseFamilyMedium() {
        return new String[][] {
                {"家和万事兴", "深入理解家庭成语、道德观念与传统习俗"},
                {"成语“望子成龙”的意思是？", "希望孩子变成龙", "希望孩子出人头地、有成就", "希望孩子长得高", "希望孩子去旅行", "希望孩子出人头地、有成就", "表达了父母对子女未来的殷切期望。"},
                {"“百善孝为先”这句话强调的是什么？", "诚实", "孝顺父母", "勇敢", "节约", "孝顺父母", "孝道是中国传统文化的根基。"},
                {"“四代同堂”在传统中国家庭中代表什么？", "人多很乱", "家庭兴旺、长寿和福气", "贫穷", "需要搬家", "家庭兴旺、长寿和福气", "四代人住在一起是令人羡慕的幸福生活。"},
                {"成语“天伦之乐”指的是什么？", "去游乐园玩", "家人团聚的快乐", "中大奖的快乐", "升职的快乐", "家人团聚的快乐", "指家庭内部亲人团聚的自然乐趣。"},
                {"中国人在哪一个节日必须回家吃“团圆饭”？", "国庆节", "春节（除夕）", "劳动节", "儿童节", "春节（除夕）", "除夕夜的团圆饭是年度最重要的家庭时刻。"},
                {"“堂”和“表”亲戚的区别是什么？", "年龄大小", "父系与母系/异姓的关系", "住得远近", "是否有钱", "父系与母系/异姓的关系", "同姓父系亲属为堂，母系或父系姐妹的后代为表。"},
                {"成语“门当户对”原指什么？", "门的大小一样", "男女双方家族社会地位和经济状况相当", "房子的方向一致", "邻居关系好", "男女双方家族社会地位和经济状况相当", "旧时指婚姻要考虑家庭背景是否匹配。"},
                {"“长辈”是指什么样的人？", "个子高的人", "家族中辈分高、年纪大的人", "力气大的人", "老师", "家族中辈分高、年纪大的人", "如爷爷、奶奶、父母等。"},
                {"在家庭餐桌上，通常谁先动筷子表示礼貌？", "最小的孩子", "饿的人", "地位最高的长辈", "客人", "地位最高的长辈", "这体现了“长幼有序”的传统礼仪。"},
                {"“重男轻女”是一种什么样的观念？", "重视男孩，轻视女孩", "男女平等", "喜欢穿红衣服", "喜欢运动", "重视男孩，轻视女孩", "这是一种过时的、不平等的传统偏见。"},
                {"“满月酒”是为什么举办的？", "庆祝结婚一周年", "庆祝孩子出生一个月", "庆祝老人大寿", "庆祝搬家", "庆祝孩子出生一个月", "祝愿新生儿健康成长。"},
                {"成语“相敬如宾”形容的是哪种关系？", "父子关系", "夫妻关系", "兄弟关系", "邻里关系", "夫妻关系", "形容夫妻互相尊敬，像对待客人一样。"},
                {"“家祭无忘告乃翁”体现了什么样的情怀？", "喜欢写诗", "对后代的叮嘱和对祖先的交代", "想吃美食", "想去钓鱼", "对后代的叮嘱和对祖先的交代", "出自陆游诗句，表现家庭责任与传承。"},
                {"中国传统中，谁负责编修“家谱”？", "小孩子", "家族中的男性长辈或族长", "政府官员", "邻居", "家族中的男性长辈或族长", "家谱记载了一个家族的世系繁衍。"},
                {"“慈母手中线，游子身上衣”表达了？", "妈妈很会缝衣服", "深厚的母爱和对远行孩子的思念", "孩子很穷", "天气很冷", "深厚的母爱和对远行孩子的思念", "唐诗经典名句，感人至深。"},
                {"“衣锦还乡”是指什么？", "穿着华丽的衣服回家（喻功成名就）", "回家洗衣服", "回家买衣服", "送衣服给亲戚", "穿着华丽的衣服回家（喻功成名就）", "指在外事业有成后荣归故里。"},
                {"“家和万事兴”告诉我们什么道理？", "家里要打扫干净", "家庭和睦才能让各方面都顺利", "要多赚钱", "要多买房子", "家庭和睦才能让各方面都顺利", "和睦是家庭幸福和成功的基础。"},
                {"“举案齐眉”是形容什么样的夫妻？", "力气很大的夫妻", "相敬相爱、感情深厚的夫妻", "喜欢吃饭的夫妻", "长得一样的夫妻", "相敬相爱、感情深厚的夫妻", "指夫妻互敬互爱。"},
                {"“膝下有儿女”中的“膝下”是指？", "膝盖下面", "父母跟前（指年幼子女陪伴）", "地毯上", "学校里", "父母跟前（指年幼子女陪伴）", "常用来形容儿女在父母身边。"},
                {"“破镜重圆”形容的是？", "修理镜子", "夫妻失散或决裂后重新团聚", "打破东西", "买新家具", "夫妻失散或决裂后重新团聚", "喻指夫妻团圆。"}
        };
    }
    public static String[][] ChineseFamilyHard() {
        return new String[][] {
                {"家族传承与礼法", "探索深层的宗法制度、祭祀礼仪与家族文化"},
                {"“宗法制度”的核心是什么？", "公平竞争", "嫡长子继承制", "抽签决定", "大家轮流", "嫡长子继承制", "这是中国古代维护家族血缘关系和权力分配的基础。"},
                {"“祠堂”是用来做什么的地方？", "吃饭", "祭祀祖先和处理族中事务", "种田", "读书", "祭祀祖先和处理族中事务", "祠堂是一个家族的灵魂与象征。"},
                {"“九族”在古代通常指什么？", "九个好朋友", "从高祖到玄孙的九代直系亲属", "九个邻居", "九个民族", "从高祖到玄孙的九代直系亲属", "常用于“株连九族”，形容范围极广。"},
                {"“五伦”关系中，关于家庭的有哪几项？", "父子、夫妇、兄弟", "君臣、朋友", "老师、学生", "医生、病人", "父子、夫妇、兄弟", "五伦是儒家规范人际关系的五种准则。"},
                {"“嫡出”与“庶出”的区别在于？", "出生地不同", "母亲的身份（正妻与妾室）", "身高不同", "智力不同", "母亲的身份（正妻与妾室）", "在古代宗法制下，两者的地位和继承权差异很大。"},
                {"“合卺礼”是指现代婚礼中的哪个环节？", "交换戒指", "喝交杯酒", "切蛋糕", "致词", "喝交杯酒", "合卺（jǐn）是古代婚礼中夫妻共饮合欢酒的仪式。"},
                {"“丧服制度”中的“斩衰”是指？", "最轻的丧服", "最重的丧服（披麻戴孝）", "平时的衣服", "漂亮的衣服", "最重的丧服（披麻戴孝）", "根据与死者关系的亲疏，丧服分为五等（五服）。"},
                {"“迁居”或“乔迁”时，为什么要请亲戚吃“暖房”酒？", "为了炫耀", "为了增加人气、驱邪避灾并联络感情", "为了收礼金", "为了打扫卫生", "为了增加人气、驱邪避灾并联络感情", "中国传统认为新房需要“人气”来旺家。"},
                {"“光宗耀祖”的含义是？", "把祖先的坟墓修亮", "为宗族争光，使祖先显耀", "给家里买灯", "学习发光技术", "为宗族争光，使祖先显耀", "这是传统价值观中个人成功的最高荣誉。"},
                {"“传宗接代”反映了什么样的家庭观念？", "重男轻女及血脉传承", "喜欢旅游", "保护环境", "科学研究", "重男轻女及血脉传承", "强调家族血缘的延续。"},
                {"“丁忧”是指古代官员在遇到什么情况时必须辞职还乡？", "生病", "父母去世", "结婚", "孩子出生", "父母去世", "古代孝道规定，父母去世官员需守丧三年。"},
                {"“分家”在传统中国家庭意味着？", "断绝关系", "兄弟各自独立门户并分割财产", "搬去旅游", "吵架", "兄弟各自独立门户并分割财产", "通常在父母去世后，兄弟之间会进行财产分割。"},
                {"“族谱”与“家谱”的关系是？", "没有关系", "族谱范围更大，包含整个宗族", "家谱范围更大", "完全一样", "族谱范围更大，包含整个宗族", "族谱是整个同姓宗族的记录。"},
                {"“克勤克俭”作为家训，主要提倡？", "勤劳和节俭", "懒惰", "浪费", "勇敢", "勤劳和节俭", "许多名门望族如曾国藩家族都以此为训。"},
                {"“连理枝”和“比翼鸟”常用来形容？", "坚贞不渝的爱情与夫妻", "好朋友", "兄弟", "父子", "坚贞不渝的爱情与夫妻", "象征夫妻永不分离。"},
                {"“入赘”是指什么现象？", "女方去男方家住", "男方结婚后定居在女方家并改姓或服劳役", "去国外住", "不结婚", "男方结婚后定居在女方家并改姓或服劳役", "也叫“倒插门”。"},
                {"“螟蛉子”在古代是指？", "亲生儿子", "养子（义子）", "孙子", "邻居的孩子", "养子（义子）", "古人误以为螟蛉是蜾蠃抚养的后代。"},
                {"“跪拜礼”在现代家庭中主要在什么场合出现？", "吃晚饭", "祭祖或极正式的拜年大礼", "洗澡", "看电视", "祭祖或极正式的拜年大礼", "表达最高等级的敬意和怀念。"},
                {"“燕尔新婚”中的“燕尔”原意是指？", "小鸟", "欢欣、快乐的样子", "讨厌", "安静", "欢欣、快乐的样子", "现常用作“新婚燕尔”，形容新婚甜蜜。"},
                {"“慎终追远”出自《论语》，其意义是？", "做事小心", "谨慎地对待葬礼，长期地思念祖先", "追求长寿", "忘记过去", "谨慎地对待葬礼，长期地思念祖先", "这被认为能使民德趋于淳厚。"}
        };
    }
    private void seedForFamilyChinese(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = ChineseFamilyEasy();
        var md = ChineseFamilyMedium();
        var h = ChineseFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] JapaneseFamilyEasy() {
        return new String[][] {
                {"私の家族", "家族の呼び方と基本的な家系図を学ぶ"},
                {"自分の父親を他人に紹介する時、何と言いますか？", "お父さん", "父（ちち）", "パパ", "おじさん", "父（ちち）", "他人に紹介する時は「父」、直接呼ぶ時は「お父さん」を使います。"},
                {"お母さんの兄弟を何と呼びますか？", "おじ", "おば", "いとこ", "おじいさん", "おじ", "父母の兄弟は「おじ」、姉妹は「おば」と言います。"},
                {"自分の兄の娘は何ですか？", "甥（おい）", "姪（めい）", "いとこ", "孫", "姪（めい）", "兄弟姉妹の娘は「姪」、息子は「甥」です。"},
                {"結婚して新しく家族になった「妻の母」を何と呼びますか？", "義理の母（義母）", "おばさん", "実母", "祖母", "義理의 母（義母）", "結婚によってできた家族には「義理の〜」を付けます。"},
                {"「兄弟姉妹（きょうだいしまい）」の中で、一番年上の女性を何と言いますか？", "妹", "姉", "弟", "兄", "姉", "自分より年上の女性の兄弟は「姉（あね）」です。"},
                {"お父さんのお父さんは誰ですか？", "おじいさん（祖父）", "おばあさん（祖母）", "おじさん", "いとこ", "おじいさん（祖父）", "父母の父は祖父（じじ・おじいさん）です。"},
                {"一人の子供しかいない家庭の子供を何と言いますか？", "一人っ子", "長男", "末っ子", "双子", "一人っ子", "兄弟がいない子供のことです。"},
                {"「配偶者（はいぐうしゃ）」とは誰のことですか？", "夫または妻", "子供", "両親", "兄弟", "夫または妻", "結婚している相手のことです。"},
                {"お正月に子供が大人からもらうお金を何と言いますか？", "お年玉", "お小遣い", "給料", "ボーナス", "お年玉", "日本の新年の伝統的な習慣です。"},
                {"「双子（ふたご）」とは何ですか？", "同じ日に生まれた二人兄弟", "三人の子供", "一番下の子供", "いとこ", "同じ日に生まれた二人兄弟", "同じ母親から一度に生まれた二人の子供です。"},
                {"自分の家系を詳しく記録した図を何と言いますか？", "地図", "家系図", "履歴書", "カレンダー", "家系図", "家族のつながりを示す図です。"}
        };
    }
    public static String[][] JapaneseFamilyMedium() {
        return new String[][] {
                {"家族の習慣と言葉", "日本の家庭文化とことわざを理解する"},
                {"ことわざ「蛙（かえる）の子は蛙」の意味は何ですか？", "子供は親に似るものだ", "子供は親より優れている", "カエルは家族が好きだ", "親と子は全く違う", "子供は親に似るものだ", "子供の性質や能力は親に似るという意味です。"},
                {"3歳、5歳、7歳の子供の成長を祝う行事は何ですか？", "お盆", "七五三（しちごさん）", "成人式", "節分", "七五三（しちごさん）", "11月15日前後に神社へお参りします。"},
                {"「反抗期（はんこうき）」とはどんな時期ですか？", "親の言うことを聞かなくなる時期", "勉強が楽しくなる時期", "たくさん寝る時期", "結婚する時期", "親の言うことを聞かなくなる時期", "子供が自立しようとして親に反発する時期です。"},
                {"お盆（おぼん）に家族が集まる主な理由は何ですか？", "先祖の霊を供養するため", "スキーに行くため", "海で泳ぐため", "学校の準備のため", "先祖の霊を供養するため", "夏に先祖の霊を迎えてお祈りする日本の伝統行事です。"},
                {"「親孝行（おやこうこう）」とはどういう意味ですか？", "親を大切にし、尽くすこと", "親を困らせること", "親を忘れること", "親と喧嘩すること", "親を大切にし、尽くすこと", "親に感謝し、親を助けることです。"},
                {"「二世帯住宅（にせたいじゅうたく）」とはどんな家ですか？", "二つの家族が一緒に住む家", "一人のための家", "店と家が一緒の建物", "車の中に住む家", "二つの家族が一緒に住む家", "親の世代と子供の世代が同じ建物で生活する家です。"},
                {"「門限（もんげん）」とは何ですか？", "家に帰らなければならない時間", "夕飯の時間", "宿題の時間", "起床時間", "家に帰らなければならない時間", "多くの家庭で決められている帰宅のルールです。"},
                {"ことわざ「可愛い子には旅をさせよ」の意味は？", "子供を甘やかさず、厳しい経験をさせるべきだ", "子供と一緒に旅行するべきだ", "子供に服を買ってあげる", "子供を一人で外に出さない", "子供を甘やかさず、厳しい経験をさせるべきだ", "本当の愛情は、自立させることだという意味です。"}
        };
    }public static String[][] JapaneseFamilyHard() {
        return new String[][] {
                {"家制度と伝統行事", "高度な家族の概念と歴史的背景を学ぶ"},
                {"日本の伝統的な「家（いえ）制度」において、家を継ぐ人を何と言いますか？", "跡取り（あととり）", "次男", "居候", "傍系", "跡取り（あととり）", "家名や財産を継承する役割の人です。"},
                {"「仏壇（ぶつだん）」とは家に置いて何をするものですか？", "先祖を祀る（まつる）ための棚", "料理を作る場所", "本を片付ける場所", "服を掛ける場所", "先祖を祀る（まつる）ための棚", "亡くなった家族や先祖にお祈りする場所です。"},
                {"結婚した時に、役所に提出する書類は何ですか？", "婚姻届（こんいんとどけ）", "出生届", "履歴書", "通知表", "婚姻届（こんいんとどけ）", "これを受理されると法律的に家族として認められます。"},
                {"「戸籍（こせき）」とは何ですか？", "個人の家族関係を公的に登録したもの", "住所の地図", "銀行の通帳", "病院のカルテ", "個人の家族関係を公的に登録したもの", "日本特有の身分登録制度です。"},
                {"「法事（ほうじ）」とは何をする行事ですか？", "亡くなった人の追善供養を行う儀式", "誕生日のお祝い", "引っ越しのお祝い", "入学式", "亡くなった人の追善供養を行う儀式", "命日などに家族や親戚が集まってお経をあげます。"},
                {"「嫡男（ちゃくなん）」とはどのような意味ですか？", "家を継ぐべき正当な長男", "親戚の子供", "末っ子の娘", "養子", "家を継ぐべき正当な長男", "歴史的に家督を継ぐ立場にある長男のことです。"},
                {"「内祝い（うちいわい）」とは本来どのような意味ですか？", "家庭内のお祝いのお返し", "家族の喧嘩", "秘密のパーティー", "外食すること", "家庭内のお祝いのお返し", "お祝いをいただいた際、感謝の気持ちとして贈る返礼品です。"},
                {"「サザエさん」のような、祖父母・父母・子供が住む家庭を何と言いますか？", "三世代同居", "核家族", "独身貴族", "共働き", "三世代同居", "三つの世代が一つ屋根の下で暮らすことです。"}
        };
    }
    private void seedForFamilyJapanese(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = JapaneseFamilyEasy();
        var md = JapaneseFamilyMedium();
        var h = JapaneseFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] KoreanFamilyEasy() {
        return new String[][] {
                {"우리 가족", "기초적인 가족 호칭과 관계를 배웁니다"},
                {"아버지의 형제를 무엇이라고 부릅니까?", "삼촌", "고모", "이모", "조카", "삼촌", "결혼하지 않은 아버지의 형제는 삼촌, 결혼하면 큰아빠나 작은아빠라고 부릅니다."},
                {"여동생이나 남동생을 통틀어 무엇이라고 합니까?", "형제", "자매", "남매", "동생", "동생", "나이가 어린 형제나 자매를 동생이라고 부릅니다."},
                {"여자가 자신의 친오빠를 부를 때 사용하는 말은?", "형", "오빠", "누나", "언니", "오빠", "여자는 오빠, 남자는 형이라고 부릅니다."},
                {"남자가 자신의 친형을 부를 때 사용하는 말은?", "형", "오빠", "누나", "언니", "형", "남자는 형, 여자는 오빠라고 부릅니다."},
                {"어머니의 자매를 무엇이라고 부릅니까?", "고모", "이모", "숙모", "조카", "이모", "어머니의 여자 형제는 이모입니다."},
                {"아버지의 자매를 무엇이라고 부릅니까?", "고모", "이모", "숙모", "조카", "고모", "아버지의 여자 형제는 고모입니다."},
                {"부모님의 부모님을 무엇이라고 합니까?", "조부모님", "손주", "조카", "사촌", "조부모님", "할아버지와 할머니를 통칭하는 말입니다."},
                {"형제나 자매가 낳은 아이를 무엇이라고 합니까?", "손자", "조카", "사촌", "사위", "조카", "나의 형제나 자매의 자녀는 조카입니다."},
                {"아내의 아버지를 부르는 말은?", "장인어른", "시아버지", "아주버님", "형님", "장인어른", "남편이 아내의 아버지를 부를 때 장인어른이라고 합니다."},
                {"남편의 어머니를 부르는 말은?", "장모님", "시어머니", "형님", "동서", "시어머니", "아내가 남편의 어머니를 부를 때 시어머니라고 합니다."},
                {"부모님 중 남자 분을 높여 부르는 말은?", "아버지", "어머니", "아들", "딸", "아버지", "아빠의 높임말은 아버지입니다."},
                {"부모님 중 여자 분을 높여 부르는 말은?", "아버지", "어머니", "아들", "딸", "어머니", "엄마의 높임말은 어머니입니다."},
                {"자신과 같은 부모 아래에서 태어난 남자 아이는?", "아들", "딸", "형제", "자매", "아들", "부모님 입장에서 남자 아이는 아들입니다."},
                {"자신과 같은 부모 아래에서 태어난 여자 아이는?", "아들", "딸", "형제", "자매", "딸", "부모님 입장에서 여자 아이는 딸입니다."},
                {"결혼한 남자와 여자를 서로 무엇이라고 합니까?", "부부", "남매", "자매", "형제", "부부", "남편과 아내를 합쳐 부부라고 합니다."},
                {"아버지의 아버지를 무엇이라고 합니까?", "할아버지", "할머니", "외할아버지", "외할머니", "할아버지", "친할아버지를 의미합니다."},
                {"어머니의 아버지를 무엇이라고 합니까?", "할아버지", "할머니", "외할아버지", "외할머니", "외할아버지", "어머니 쪽 할아버지는 외할아버지입니다."},
                {"여자가 언니의 남편을 부르는 말은?", "형부", "제부", "아주버님", "도련님", "형부", "언니의 남편은 형부입니다."},
                {"남자가 형의 아내를 부르는 말은?", "형수님", "처남", "매제", "동서", "형수님", "형의 아내는 형수님이라고 부릅니다."},
                {"자녀의 자녀를 무엇이라고 합니까?", "손주", "부모", "조카", "사촌", "손주", "아들이나 딸이 낳은 아이를 손주라고 합니다."}
        };
    }public static String[][] KoreanFamilyMedium() {
        return new String[][] {
                {"가족 문화와 풍습", "한국의 독특한 가족 문화와 속담을 이해합니다"},
                {"설날에 어른들께 절을 하며 드리는 인사를 무엇이라고 합니까?", "세배", "차례", "성묘", "돌잔치", "세배", "새해를 맞아 어른들께 건강을 기원하며 절을 하는 풍습입니다."},
                {"추석에 온 가족이 모여 만드는 전통 음식은?", "떡국", "송편", "비빔밥", "잡채", "송편", "추석에는 햅쌀로 송편을 빚어 먹습니다."},
                {"아이가 태어난 지 1년이 되었을 때 여는 잔치는?", "환갑잔치", "돌잔치", "칠순잔치", "결혼잔치", "돌잔치", "아이의 건강과 복을 빌어주는 첫 번째 생일 파티입니다."},
                {"한국에서 부모님께 효도하는 마음을 무엇이라고 합니까?", "효도", "우애", "협동", "정직", "효도", "부모님을 정성껏 모시는 마음인 효(孝)를 중요하게 여깁니다."},
                {"'피는 물보다 진하다'라는 속담의 뜻은?", "물이 맛있다", "가족의 유대감이 가장 강하다", "피가 무섭다", "수영을 잘한다", "가족의 유대감이 가장 강하다", "가족 간의 혈연 관계가 무엇보다 중요하다는 뜻입니다."},
                {"설날에 먹는 대표적인 음식으로, 한 살을 더 먹는다는 의미가 있는 음식은?", "미역국", "떡국", "삼계탕", "김치찌개", "떡국", "설날 아침에 떡국을 먹으며 새해를 시작합니다."},
                {"부모님이 돌아가신 날 매년 드리는 의식은?", "제사", "생일", "소풍", "운동회", "제사", "조상을 기억하고 정성을 다해 음식을 차리는 의식입니다."},
                {"결혼한 여자가 시댁에 가서 처음 드리는 인사를 무엇이라고 합니까?", "폐백", "함", "예물", "축의금", "폐백", "신부가 시부모님과 친척들께 정식으로 드리는 인사입니다."},
                {"한국의 어버이날은 몇 월 며칠입니까?", "5월 5일", "5월 8일", "5월 15일", "6월 6일", "5월 8일", "부모님의 은혜에 감사하는 날입니다."},
                {"형제 사이에 서로 아끼고 사랑하는 마음을 무엇이라고 합니까?", "효도", "우애", "경애", "충성", "우애", "형제나 자매 사이의 두터운 정을 의미합니다."},
                {"아이의 장래를 점치기 위해 돌잔치 때 여러 물건을 놓는 행사는?", "돌잡이", "숨바꼭질", "제기차기", "줄다리기", "돌잡이", "아이 앞에 실, 돈, 연필 등을 놓아 무엇을 잡는지 봅니다."},
                {"부모님의 60번째 생일을 기념하는 잔치는?", "돌잔치", "환갑잔치", "칠순잔치", "팔순잔치", "환갑잔치", "과거에는 장수를 축하하며 크게 열었던 잔치입니다."},
                {"한국 가족 문화의 가장 큰 특징 중 하나로, 부모님을 모시고 사는 형태는?", "핵가족", "대가족", "1인 가구", "딩크족", "대가족", "여러 세대가 함께 모여 사는 형태를 말합니다."},
                {"집안의 어른이 돌아가셨을 때 입는 흰색이나 검은색 옷을 무엇이라고 합니까?", "상복", "한복", "예복", "교복", "상복", "장례식에서 유가족들이 입는 옷입니다."},
                {"결혼식에서 하객들이 축하의 의미로 내는 돈은?", "조의금", "축의금", "세뱃돈", "용돈", "축의금", "결혼을 축하하며 건네는 성금입니다."},
                {"조상의 묘에 가서 인사를 드리고 주변을 살피는 일은?", "성묘", "산책", "등산", "이사", "성묘", "명절이나 기일에 조상의 산소를 찾는 일입니다."},
                {"'가지 많은 나무에 바람 잘 날 없다'라는 속담의 뜻은?", "나무가 크다", "자식이 많으면 걱정이 끊이지 않는다", "바람이 시원하다", "숲에 나무가 많다", "자식이 많으면 걱정이 끊이지 않는다", "가족이 많으면 그만큼 신경 쓸 일이 많다는 뜻입니다."},
                {"아기가 태어나기 전, 부모나 친척이 꾸는 예지몽은?", "개꿈", "태몽", "길몽", "악몽", "태몽", "아이의 성별이나 앞날을 암시한다고 믿는 꿈입니다."},
                {"어머니가 아이를 낳고 몸조리를 하며 먹는 음식은?", "떡볶이", "미역국", "냉면", "삼겹살", "미역국", "칼슘과 요오드가 풍부해 산모의 회복을 돕는 음식입니다."},
                {"추석 때 밤하늘을 보며 가족의 건강을 비는 대상은?", "태양", "보름달", "별", "구름", "보름달", "추석 보름달을 보며 소원을 비는 풍습이 있습니다."}
        };
    }public static String[][] KoreanFamilyHard() {
        return new String[][] {
                {"가문과 계보", "한국의 족보 시스템과 심화된 가족 예법을 탐구합니다"},
                {"한 가문의 계통과 혈연 관계를 기록한 책은?", "일기장", "족보", "자서전", "교과서", "족보", "조상부터 내려오는 가족의 역사를 기록한 문서입니다."},
                {"본관(本貫)이란 무엇을 의미합니까?", "현재 사는 곳", "시조가 태어난 고향", "학교 이름", "직업 이름", "시조가 태어난 고향", "김해 김씨, 경주 이씨처럼 가문의 뿌리가 되는 지명을 말합니다."},
                {"부계 사회에서 가문의 대를 잇는 아들을 무엇이라고 합니까?", "장남", "종손", "차남", "양자", "종손", "종갓집의 대를 잇는 큰 종가의 장손을 의미합니다."},
                {"'사촌'보다 먼 '육촌'이나 '팔촌'을 통틀어 부르는 말은?", "남남", "당숙", "일가친척", "사돈", "일가친척", "혈연 관계가 있는 가까운 집안사람들을 의미합니다."},
                {"결혼을 통해 맺어진 상대방의 가족 관계를 무엇이라고 합니까?", "혈연", "인척", "직계", "방계", "인척", "혼인에 의해 생기는 친족 관계를 말합니다."},
                {"자신으로부터 직선으로 올라가거나 내려가는 혈족은?", "직계혈족", "방계혈족", "사돈", "외척", "직계혈족", "부모, 조부모, 자녀, 손자 등을 말합니다."},
                {"형제나 자매의 자녀를 부르는 또 다른 한자어는?", "조카", "질녀/질자", "손주", "사위", "질녀/질자", "공식적인 문서나 한자어로 조카를 지칭할 때 사용합니다."},
                {"유교 예법에서 돌아가신 지 3년이 되는 날 드리는 제사는?", "소상", "대상", "삼우제", "사십구재", "대상", "부모님이 돌아가신 지 만 2년(3년째)이 되는 날 지내는 제사입니다."},
                {"같은 성씨와 같은 본관을 가진 사람들을 무엇이라고 합니까?", "동성동본", "이성동본", "동성본관", "이성본관", "동성동본", "과거에는 동성동본 사이의 결혼이 법으로 금지되기도 했습니다."},
                {"혼인신고를 하기 전, 양가 부모님이 정식으로 만나 인사하는 자리는?", "상견례", "결혼식", "약혼식", "피로연", "상견례", "양측 집안이 가족이 되기 위해 처음 인사하는 엄숙한 자리입니다."},
                {"남편의 남동생이 결혼하기 전 부르는 호칭은?", "도련님", "서방님", "아주버님", "처남", "도련님", "결혼 후에는 서방님이라고 부르기도 합니다."},
                {"아내의 남동생을 부르는 호칭은?", "처남", "처제", "동서", "형부", "처남", "아내의 남자 형제는 처남입니다."},
                {"가족 관계를 증명하기 위해 동사무소에서 발급받는 서류는?", "가족관계증명서", "건강검진표", "성적표", "면허증", "가족관계증명서", "부모, 배우자, 자녀의 인적 사항이 기록된 공문서입니다."},
                {"'촌수'를 계산할 때 부모와 자식 사이는 몇 촌입니까?", "1촌", "2촌", "3촌", "4촌", "1촌", "가장 가까운 직계 관계입니다."},
                {"형제와 자매 사이의 촌수는 몇 촌입니까?", "1촌", "2촌", "3촌", "4촌", "2촌", "부모를 거쳐 계산되므로 2촌이 됩니다."},
                {"전통 혼례에서 신랑이 신부 집에 기러기를 전달하는 의식은?", "전안례", "대례", "교배례", "합근례", "전안례", "변치 않는 사랑을 약속하는 의미가 담겨 있습니다."},
                {"가문에서 전해 내려오는 가르침이나 규칙을 무엇이라고 합니까?", "가풍", "가훈", "가보", "가업", "가훈", "가족 구성원들이 지켜야 할 도덕적 지침입니다."},
                {"남편의 형을 부르는 호칭은?", "아주버님", "도련님", "서방님", "처남", "아주버님", "아내가 남편의 형을 부를 때 사용합니다."},
                {"'고부갈등'이란 누구와 누구 사이의 갈등을 의미합니까?", "부부 사이", "시어머니와 며느리", "장모와 사위", "형제 사이", "시어머니와 며느리", "한국 사회의 전통적인 가족 문제 중 하나를 지칭하는 용어입니다."},
                {"돌아가신 조상의 묘소를 관리하고 제사를 지내는 집을 무엇이라고 합니까?", "종가", "분가", "처가", "시가", "종가", "한 가문의 대종손이 사는 유서 깊은 집을 말합니다."}
        };
    }
    private void seedForFamilyKorean(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = KoreanFamilyEasy();
        var md = KoreanFamilyMedium();
        var h = KoreanFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] HindiFamilyEasy() {
        return new String[][] {
                {"मेरा परिवार", "बुनियादी पारिवारिक रिश्तों और शब्दावली को समझें"},
                {"पिता के पिता को क्या कहते हैं?", "दादाजी", "नानाजी", "ताऊजी", "मामाजी", "दादाजी", "पिता के पिता दादाजी और माँ के पिता नानाजी कहलाते हैं।"},
                {"माँ की माँ को क्या कहते हैं?", "दादीजी", "नानीजी", "मौसी", "चाची", "नानीजी", "माँ की माँ को नानीजी कहा जाता है।"},
                {"पिता के छोटे भाई को क्या कहते हैं?", "ताऊजी", "चाचाजी", "मामाजी", "फूफाजी", "चाचाजी", "पिता के छोटे भाई चाचाजी और बड़े भाई ताऊजी कहलाते हैं।"},
                {"माँ के भाई को क्या कहते हैं?", "चाचाजी", "ताऊजी", "मामाजी", "मौसाजी", "मामाजी", "माँ के भाई को मामाजी कहा जाता है।"},
                {"पिता की बहन को क्या कहते हैं?", "मौसी", "बुआ", "चाची", "मामी", "बुआ", "पिता की बहन बुआ कहलाती हैं।"},
                {"माँ की बहन को क्या कहते हैं?", "बुआ", "मामी", "मौसी", "दादी", "मौसी", "माँ की बहन को मौसी कहा जाता है।"},
                {"भाई की बेटी आपकी क्या लगेगी?", "भतीजी", "भांजी", "पोती", "बेटी", "भतीजी", "भाई की बेटी भतीजी और बहन की बेटी भांजी कहलाती है।"},
                {"बहन के बेटे को क्या कहते हैं?", "भतीजा", "भांजा", "बेटा", "पोता", "भांजा", "बहन का बेटा भांजा कहलाता है।"},
                {"पति की माँ को क्या कहते हैं?", "दादी", "नानी", "सास", "चाची", "सास", "पति या पत्नी की माँ को सास कहा जाता है।"},
                {"पत्नी के पिता को क्या कहते हैं?", "ससुर", "ताऊ", "मामा", "चाचा", "ससुर", "पति या पत्नी के पिता को ससुर कहा जाता है।"},
                {"बेटे की पत्नी को क्या कहते हैं?", "बेटी", "बहु", "भाभी", "ननद", "बहु", "बेटे की पत्नी को बहु कहा जाता है।"},
                {"बेटी के पति को क्या कहते हैं?", "दामाद", "साला", "देवर", "भाई", "दामाद", "बेटी के पति को दामाद कहा जाता है।"},
                {"एक ही माता-पिता की संतानों को क्या कहते हैं?", "दोस्त", "भाई-बहन", "रिश्तेदार", "पड़ोसी", "भाई-बहन", "इन्हें अंग्रेजी में 'Siblings' कहा जाता है।"},
                {"शादी के बाद महिला का नया घर क्या कहलाता है?", "मायका", "ससुराल", "ननिहाल", "हॉस्टल", "ससुराल", "महिला के पति का घर उसका ससुराल होता है।"},
                {"बच्चों के लिए माँ का घर (बचपन का घर) क्या कहलाता है?", "ससुराल", "ननिहाल", "मायका", "ददिहाल", "ननिहाल", "जहाँ नाना-नानी रहते हैं, उसे ननिहाल कहते हैं।"},
                {"भाई की पत्नी को क्या कहते हैं?", "दीदी", "भाभी", "मौसी", "चाची", "भाभी", "बड़े भाई की पत्नी को भाभी कहा जाता है।"},
                {"पिता के बड़े भाई को क्या कहते हैं?", "चाचा", "ताऊजी", "मामा", "मौसा", "ताऊजी", "उत्तर भारत में पिता के बड़े भाई को ताऊजी कहते हैं।"},
                {"आपके पुत्र का पुत्र आपका क्या लगेगा?", "पोता", "नाती", "भतीजा", "बेटा", "पोता", "बेटे का बेटा पोता कहलाता है।"},
                {"आपकी पुत्री की पुत्री आपकी क्या लगेगी?", "पोती", "नातिन", "भतीजी", "बेटी", "नातिन", "बेटी की बेटी को नातिन कहा जाता है।"},
                {"पति के छोटे भाई को क्या कहते हैं?", "जेठ", "देवर", "साला", "नंदोई", "देवर", "पति का छोटा भाई देवर और बड़ा भाई जेठ कहलाता है।"}
        };
    }public static String[][] HindiFamilyMedium() {
        return new String[][] {
                {"संस्कृति और परंपरा", "भारतीय पारिवारिक मूल्यों और त्योहारों को समझें"},
                {"भाई-बहन के प्रेम का मुख्य त्योहार कौन सा है?", "दीवाली", "होली", "रक्षाबंधन", "ईद", "रक्षाबंधन", "इस दिन बहन भाई की कलाई पर राखी बांधती है।"},
                {"'संयुक्त परिवार' (Joint Family) का क्या अर्थ है?", "अकेले रहना", "जहाँ दादा-दादी, माता-पिता और चाचा-चाची साथ रहते हैं", "सिर्फ दोस्तों के साथ रहना", "विदेश में रहना", "जहाँ दादा-दादी, माता-पिता और चाचा-चाची साथ रहते हैं", "भारत में बड़े परिवारों का साथ रहना एक परंपरा है।"},
                {"बड़ों के सम्मान में भारत में क्या किया जाता है?", "हाथ मिलाना", "पैर छूना", "नमस्ते करना", "गले मिलना", "पैर छूना", "बड़ों का आशीर्वाद लेने के लिए उनके चरण स्पर्श किए जाते हैं।"},
                {"'अतिथि देवो भव' का क्या अर्थ है?", "मेहमान भगवान के समान होता है", "मेहमान को घर नहीं बुलाना", "मेहमान से काम कराना", "मेहमान को भूल जाना", "मेहमान भगवान के समान होता है", "यह भारतीय अतिथि सत्कार का मूल मंत्र है।"},
                {"शादी के समय दूल्हे के भाई को क्या कहा जाता है?", "साला", "सरहज", "शहबाला", "देवर", "शहबाला", "दूल्हे के साथ रहने वाले छोटे बच्चे या भाई को शहबाला कहते हैं।"},
                {"'संस्कार' शब्द का परिवार में क्या महत्व है?", "सिर्फ पूजा करना", "अच्छी शिक्षा और नैतिक मूल्य", "पैसे कमाना", "खाना बनाना", "अच्छी शिक्षा और नैतिक मूल्य", "परिवार बच्चों को अच्छे संस्कार देने की कोशिश करता है।"},
                {"दीवाली पर परिवार मिलकर किसकी पूजा करते हैं?", "हनुमान जी", "लक्ष्मी-गणेश जी", "शिव जी", "सूर्य देव", "लक्ष्मी-गणेश जी", "सुख और समृद्धि के लिए यह पूजा की जाती है।"},
                {"'कन्यादान' रस्म किस अवसर पर होती है?", "जन्मदिन", "नामकरण", "विवाह", "मुंडन", "विवाह", "पिता द्वारा अपनी पुत्री को दूल्हे को सौंपने की रस्म।"},
                {"बच्चे के जन्म के बाद सिर के बाल काटने की रस्म क्या कहलाती है?", "विवाह", "मुंडन", "जनेऊ", "सगाई", "मुंडन", "यह एक महत्वपूर्ण हिंदू संस्कार है।"},
                {"'मायका' शब्द का क्या अर्थ है?", "पति का घर", "स्त्री के माता-पिता का घर", "पड़ोसी का घर", "मामा का घर", "स्त्री के माता-पिता का घर", "शादी के बाद महिला अपने माता-पिता के घर को मायका कहती है।"},
                {"भाई की पत्नी के भाई को क्या कहेंगे?", "साला", "भाई", "समधी", "कोई रिश्ता नहीं", "साला", "रिश्तेदारी में इसे अक्सर साला ही संबोधित किया जाता है।"},
                {"शादी से पहले की रस्म जिसमें उपहार दिए जाते हैं, क्या कहलाती है?", "सगाई", "विदाई", "श्राद्ध", "तेरहवीं", "सगाई", "इसे 'रोका' या 'सगाई' कहा जाता है।"},
                {"बहन के पति को क्या कहते हैं?", "देवर", "जीजाजी", "साला", "जेठ", "जीजाजी", "बहन के पति को जीजाजी कहा जाता है।"},
                {"पत्नी की बहन को क्या कहते हैं?", "ननद", "साली", "भाभी", "जेठानी", "साली", "पत्नी की बहन साली कहलाती है।"},
                {"पति की बहन को क्या कहते हैं?", "साली", "ननद", "देवरानी", "चाची", "ननद", "पति की बहन ननद कहलाती है।"},
                {"'पगड़ी रस्म' परिवार में कब होती है?", "शादी में", "बुजुर्ग की मृत्यु के बाद जिम्मेदारी सौंपने पर", "जन्मदिन पर", "तीज पर", "बुजुर्ग की मृत्यु के बाद जिम्मेदारी सौंपने पर", "यह परिवार के नए मुखिया की घोषणा होती है।"},
                {"'करवा चौथ' का व्रत महिलाएँ किसके लिए रखती हैं?", "बच्चों के लिए", "पति की लंबी उम्र के लिए", "भाई के लिए", "माता-पिता के लिए", "पति की लंबी उम्र के लिए", "यह उत्तर भारत का एक प्रसिद्ध त्योहार है।"},
                {"'भैया दूज' का त्योहार कब मनाया जाता है?", "होली के बाद", "दीवाली के दो दिन बाद", "राखी के दिन", "मकर संक्रांति पर", "दीवाली के दो दिन बाद", "यह भाई-बहन के प्रेम का त्योहार है।"},
                {"छठ पूजा मुख्य रूप से किस राज्य का पारिवारिक त्योहार है?", "पंजाब", "बिहार", "केरल", "गुजरात", "बिहार", "यह परिवार की सुख-शांति के लिए मनाया जाता है।"},
                {"शादी के बाद दूल्हे का स्वागत वधू पक्ष द्वारा क्या कहलाता है?", "बारात", "अगवानी/द्वार पूजा", "विदाई", "जयमाला", "अगवानी/द्वार पूजा", "दूल्हे का दरवाजे पर स्वागत करना।"}
        };
    }public static String[][] HindiFamilyHard() {
        return new String[][] {
                {"वंश और उत्तराधिकार", "गहन पारिवारिक संबंधों और कानूनी शब्दावली का अन्वेषण"},
                {"'वंशावली' (Genealogy) का क्या अर्थ है?", "भोजन की सूची", "परिवार के पूर्वजों का इतिहास", "शहर का नक्शा", "दोस्तों का समूह", "परिवार के पूर्वजों का इतिहास", "पीढ़ियों के रिकॉर्ड को वंशावली कहते हैं।"},
                {"'सपिंड' शब्द का अर्थ किस संबंध से है?", "मित्रता", "एक ही पिंड या रक्त से जुड़े रिश्तेदार", "पड़ोसी", "गुरु-शिष्य", "एक ही पिंड या रक्त से जुड़े रिश्तेदार", "यह हिंदू विवाह कानून में महत्वपूर्ण है।"},
                {"'उत्तराधिकारी' किसे कहते हैं?", "जो संपत्ति छोड़ दे", "जिसे कानूनी रूप से संपत्ति प्राप्त हो", "जो किराएदार हो", "जो पड़ोसी हो", "जिसे कानूनी रूप से संपत्ति प्राप्त हो", "माता-पिता के बाद उनकी संपत्ति का मालिक।"},
                {"'गोत्र' शब्द का क्या महत्व है?", "पसंदीदा रंग", "ऋषियों के नाम पर आधारित वंश की पहचान", "पसंदीदा खेल", "भोजन का प्रकार", "ऋषियों के नाम पर आधारित वंश की पहचान", "हिंदू धर्म में विवाह के समय गोत्र मिलाया जाता है।"},
                {"'दत्तक पुत्र' किसे कहा जाता है?", "सगा बेटा", "गोद लिया हुआ पुत्र", "सौतेला बेटा", "भतीजा", "गोद लिया हुआ पुत्र", "कानूनी रूप से अपनाया गया बच्चा।"},
                {"पिता के ताऊ के बेटे को आप क्या कहेंगे?", "भाई", "ताऊ", "चाचा", "मामा", "चाचा", "रिश्ते में वह आपके चाचा लगेंगे।"},
                {"'पित्रृसत्ता' (Patriarchy) का क्या अर्थ है?", "बच्चों का शासन", "पुरुष प्रधान परिवार", "महिला प्रधान परिवार", "दोस्तों का शासन", "पुरुष प्रधान परिवार", "जहाँ परिवार का मुखिया पुरुष होता है।"},
                {"'वसीयत' (Will) क्या होती है?", "शादी का कार्ड", "मृत्यु के बाद संपत्ति के बंटवारे का दस्तावेज", "जन्म प्रमाण पत्र", "राशन कार्ड", "मृत्यु के बाद संपत्ति के बंटवारे का दस्तावेज", "व्यक्ति की अंतिम इच्छा का कानूनी पत्र।"},
                {"'पुश्तैनी' शब्द का क्या अर्थ है?", "नया खरीदा हुआ", "पूर्वजों से प्राप्त", "किराए का", "खोया हुआ", "पूर्वजों से प्राप्त", "जो संपत्ति या गुण पीढ़ियों से चले आ रहे हों।"},
                {"'कुनबा' किसे कहते हैं?", "एक व्यक्ति", "पूरा परिवार या खानदान", "बाजार", "अस्पताल", "पूरा परिवार या खानदान", "यह एक बड़े विस्तारित परिवार के लिए इस्तेमाल होता है।"},
                {"'सौतेली माँ' के लिए हिंदी शब्द क्या है?", "सगी माँ", "विमाता", "मौसी", "दादी", "विमाता", "साहित्यिक हिंदी में सौतेली माँ को विमाता कहते हैं।"},
                {"'परदादा' कौन होते हैं?", "दादा के पिता", "पिता के पिता", "नाना के पिता", "भाई", "दादा के पिता", "पिता के दादा को परदादा कहते हैं।"},
                {"'समधी' किसे कहते हैं?", "भाई को", "बेटी या बेटे के ससुर को", "दोस्त को", "जीजा को", "बेटी या बेटे के ससुर को", "दो परिवारों के बीच वैवाहिक संबंध जोड़ने वाले पिता।"},
                {"'देवरानी-जेठानी' का रिश्ता क्या है?", "दो बहनें", "दो भाइयों की पत्नियाँ", "माँ और बेटी", "सास और बहु", "दो भाइयों की पत्नियाँ", "पति के भाइयों की पत्नियाँ आपस में ये कहलाती हैं।"},
                {"'नातिन' किसे कहते हैं?", "बेटे की बेटी", "बेटी की बेटी", "भाई की बेटी", "बहन की बेटी", "बेटी की बेटी", "पुत्री की पुत्री को नातिन कहा जाता है।"},
                {"'वंशज' (Descendant) का क्या अर्थ है?", "पूर्वज", "आने वाली पीढ़ी के सदस्य", "गुरु", "दुश्मन", "आने वाली पीढ़ी के सदस्य", "एक ही परिवार की अगली कड़ियाँ।"},
                {"'श्राद्ध' की रस्म क्यों की जाती है?", "शादी के लिए", "पूर्वजों की आत्मा की शांति के लिए", "जन्मदिन के लिए", "व्यापार के लिए", "पूर्वजों की आत्मा की शांति के लिए", "यह मृत पूर्वजों के प्रति सम्मान व्यक्त करने का समय है।"},
                {"'पितृ' शब्द किसके लिए प्रयुक्त होता है?", "बच्चों के लिए", "पूर्वजों या पिताओं के लिए", "महिलाओं के लिए", "मित्रों के लिए", "पूर्वजों या पिताओं के लिए", "आध्यात्मिक रूप से मृत पूर्वजों को पितृ कहते हैं।"},
                {"'अविभाजित परिवार' (HUF) का कानूनी अर्थ क्या है?", "हिंदू अविभाजित परिवार", "दोस्त", "कंपनी", "स्कूल", "हिंदू अविभाजित परिवार", "भारतीय कर कानून में एक विशेष इकाई।"},
                {"'कुल' शब्द का क्या अर्थ है?", "ठंडा", "वंश या परिवार", "सब कुछ", "पानी", "वंश या परिवार", "जैसे 'रघुकुल' या परिवार की मर्यादा।"}
        };
    }
    private void seedForFamilyHindi(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = HindiFamilyEasy();
        var md = HindiFamilyMedium();
        var h = HindiFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] RussianFamilyEasy() {
        return new String[][] {
                {"Моя семья", "Изучаем основные термины родства и стандартные обращения"},
                {"Как называют брата отца или матери?", "Дядя", "Племянник", "Двоюродный брат", "Зять", "Дядя", "Братья ваших родителей — это ваши дяди."},
                {"Дочь вашего брата или сестры — это ваша...", "Племянница", "Внучка", "Сестра", "Мачеха", "Племянница", "Ребенок ваших братьев или сестер женского пола — это племянница."},
                {"Как называется семья, состоящая из родителей и детей?", "Нуклеарная семья", "Многопоколенная семья", "Приемная семья", "Одинокая семья", "Нуклеарная семья", "Это базовая ячейка общества: родители и их дети."},
                {"Мать мужа для жены — это...", "Свекровь", "Теща", "Мачеха", "Крестная", "Свекровь", "Мать мужа называется свекровью, а мать жены — тещей."},
                {"Как называют ребенка мужского пола?", "Сын", "Дочь", "Брат", "Внук", "Сын", "Мальчик по отношению к родителям — это сын."},
                {"Кто такой 'двоюродный брат'?", "Сын тети или дяди", "Сын брата", "Брат отца", "Сводный брат", "Сын тети или дяди", "Дети ваших родных дядей и тетей — это ваши двоюродные братья и сестры."},
                {"Если отец женится второй раз, его новая жена — это ваша...", "Тетя", "Мачеха", "Свекровь", "Крестная", "Мачеха", "Неродная мать в новом браке отца называется мачехой."},
                {"Как называют человека, с которым вы помолвлены?", "Супруг", "Жених / Невеста", "Партнер", "Родственник", "Жених / Невеста", "Это те, кто собирается вступить в брак."},
                {"Кто такие 'братья и сестры' одним словом?", "Сиблинги", "Родственники", "Двойняшки", "Родители", "Сиблинги", "В науке используется термин 'сиблинги', но обычно говорят 'родные братья и сестры'."},
                {"Как называют отца отца?", "Дедушка", "Прадедушка", "Дядя", "Отчим", "Дедушка", "Отец одного из ваших родителей — это ваш дедушка."},
                {"Как называют единственного ребенка в семье?", "Единственный ребенок", "Первенец", "Младший", "Сирота", "Единственный ребенок", "У такого ребенка нет братьев и сестер."},
                {"Кто такой 'супруг'?", "Брат", "Муж или жена", "Отец", "Сосед", "Муж или жена", "Это официальный термин для человека, с которым вы состоите в браке."},
                {"Сын вашего сына — это ваш...", "Племянник", "Правнук", "Внук", "Кузен", "Внук", "Ребенок вашего сына или дочери — это внук."},
                {"Кто такой 'отчим'?", "Муж матери (неродной отец)", "Брат отца", "Свекор", "Дедушка", "Муж матери (неродной отец)", "Неродной отец, ставший членом семьи через брак с матерью."},
                {"Как называют дочку сестры или брата?", "Племянница", "Внучка", "Сестра", "Кузина", "Племянница", "Племянница — это дочь вашего брата или сестры."},
                {"Как называют женщину, у которой умер муж?", "Вдова", "Разведенная", "Мать-одиночка", "Невеста", "Вдова", "Мужчина в такой ситуации называется вдовцом."},
                {"Как ласково называют бабушку в России?", "Бабуля", "Тетя", "Мамуля", "Няня", "Бабуля", "Бабуля или бабушка — самые распространенные обращения."},
                {"Кто такие 'близнецы'?", "Дети, родившиеся одновременно у одной матери", "Двоюродные братья", "Лучшие друзья", "Соседи", "Дети, родившиеся одновременно у одной матери", "Близнецы могут быть очень похожи друг на друга."},
                {"Отец жены для мужа — это...", "Тесть", "Свекор", "Отчим", "Деверь", "Тесть", "Отец жены называется тесть, а отец мужа — свекор."},
                {"Как называют первого родившегося ребенка в семье?", "Первенец", "Младший", "Наследник", "Внук", "Первенец", "Это самый старший ребенок среди братьев и сестер."}
        };
    }public static String[][] RussianFamilyMedium() {
        return new String[][] {
            {"Семейные традиции и идиомы", "Понимание русских пословиц и социальных обычаев"},
            {"Что означает пословица 'Яблоко от яблони недалеко падает'?", "Дети не похожи на родителей", "Дети похожи на своих родителей", "Яблоки полезны для семьи", "Семья должна жить в лесу", "Дети похожи на своих родителей", "Это означает, что дети часто наследуют качества своих родителей."},
            {"Что такое 'семейная реликвия'?", "Старая мебель", "Предмет, передающийся из поколения в поколение", "Подарок от соседа", "Новый автомобиль", "Предмет, передающийся из поколения в поколение", "Это вещь, которая дорога семье как память о предках."},
            {"Что значит быть 'белой вороной' в семье?", "Быть любимчиком", "Быть непохожим на других членов семьи", "Быть самым старшим", "Быть самым богатым", "Быть непохожим на других членов семьи", "Так говорят о человеке, который сильно отличается по взглядам или поведению."},
            {"Кто такой 'кормилец' семьи?", "Тот, кто готовит еду", "Тот, кто зарабатывает деньги на нужды семьи", "Самый младший ребенок", "Домашний питомец", "Тот, кто зарабатывает деньги на нужды семьи", "Это человек, обеспечивающий основной доход домохозяйства."},
            {"Какое дерево в России считается символом семьи и корней?", "Пальма", "Береза", "Родовое древо", "Елка", "Родовое древо", "Схему поколений часто рисуют в виде дерева с корнями и ветвями."},
            {"Что означает выражение 'Кровь — не водица'?", "Вода важнее", "Семейные узы очень сильны", "Кровь трудно отмыть", "Друзья важнее семьи", "Семейные узы очень сильны", "Это подчеркивает важность родственных связей."},
            {"Кто такие 'крестные родители'?", "Случайные прохожие", "Духовные наставники ребенка при крещении", "Биологические родители", "Врачи", "Духовные наставники ребенка при крещении", "Крестные (кумовья) играют важную роль в традиционной культуре."},
            {"Как в России называют праздник 8 Марта?", "День отца", "Международный женский день", "День знаний", "День защиты детей", "Международный женский день", "В этот день поздравляют матерей, бабушек, сестер и жен."},
            {"Что такое 'семейный совет'?", "Ссора в семье", "Обсуждение важных вопросов всей семьей", "Урок в школе", "Просмотр телевизора", "Обсуждение важных вопросов всей семьей", "Это когда семья собирается вместе для принятия решений."},
            {"Что означает идиома 'Выносить сор из избы'?", "Делать уборку", "Рассказывать о семейных проблемах посторонним", "Выбрасывать мусор", "Переезжать в новый дом", "Рассказывать о семейных проблемах посторонним", "Считается, что семейные ссоры не должны обсуждаться с чужими."},
            {"Что такое 'домочадцы'?", "Люди, строящие дома", "Все, кто живет вместе в одном доме", "Соседи по улице", "Работники в офисе", "Все, кто живет вместе в одном доме", "Это старое и теплое слово для обозначения членов семьи."},
            {"Как называют свадьбу после 50 лет совместной жизни?", "Серебряная", "Золотая", "Бриллиантовая", "Деревянная", "Золотая", "Серебряная свадьба отмечается через 25 лет, а золотая — через 50."},
            {"Что означает 'жить под одним крылом'?", "Летать на самолете", "Жить под защитой или покровительством старшего", "Чинить крышу", "Путешествовать", "Жить под защитой или покровительством старшего", "Обычно речь идет о детях под опекой родителей."},
            {"Какое отчество будет у ребенка, если отца зовут Иван?", "Иванович / Ивановна", "Петрович / Петровна", "Васильевич", "Иванов", "Иванович / Ивановна", "В России отчество образуется от имени отца."},
            {"Что принято дарить на 'ситцевую свадьбу' (1 год брака)?", "Золото", "Изделия из ситца (ткани)", "Машину", "Цветы", "Изделия из ситца (ткани)", "Каждая годовщина в России имеет свое название и символику."},
            {"Что такое 'династия'?", "Тип спортивной команды", "Ряд поколений, продолжающих дело предков", "Название ресторана", "Вид одежды", "Ряд поколений, продолжающих дело предков", "Например, династия врачей или учителей."},
            {"Что означает 'быть на седьмом небе от счастья'?", "Улететь в космос", "Испытывать огромную радость в кругу семьи", "Быть на высоком этаже", "Заблудиться", "Испытывать огромную радость в кругу семьи", "Часто используется при описании важных семейных событий."},
            {"Как называют родителей мужа и жены по отношению друг к другу?", "Сваты", "Друзья", "Коллеги", "Соперники", "Сваты", "Родители супругов — это сваты."},
            {"Кто такая 'хранительница очага'?", "Пожарный", "Женщина, поддерживающая уют и гармонию в доме", "Повар в ресторане", "Продавец", "Женщина, поддерживающая уют и гармонию в доме", "Традиционная роль женщины в семье."},
            {"Что такое 'традиционное воскресное застолье'?", "Одинокий ужин", "Обед, на который собирается вся семья", "Работа на даче", "Прогулка в парке", "Обед, на который собирается вся семья", "Важный ритуал для укрепления семейных связей."}
        };
    }public static String[][] RussianFamilyHard() {
        return new String[][] {
                {"Наследие и право", "Сложные родственные связи, юридические аспекты и генеалогия"},
                {"Кто такой 'деверь'?", "Брат мужа", "Брат жены", "Муж сестры", "Отец мужа", "Брат мужа", "В русском языке есть специфические названия для всех родственников супругов."},
                {"Как называют сестру жены?", "Свояченица", "Золовка", "Сноха", "Шурин", "Свояченица", "Сестра жены — свояченица, сестра мужа — золовка."},
                {"Кто такой 'шурин'?", "Брат жены", "Брат мужа", "Сын сестры", "Свекор", "Брат жены", "Шурин — это родной брат супруги."},
                {"Что такое 'генеалогия'?", "Наука о растениях", "Наука о родословной человека", "Изучение космоса", "Тип медицины", "Наука о родословной человека", "Это изучение истории своей семьи и предков."},
                {"Кто является 'ближайшим родственником' по закону?", "Лучший друг", "Супруг, дети или родители", "Сосед по квартире", "Адвокат", "Супруг, дети или родители", "Юридически это первая очередь наследников."},
                {"Что такое 'приемная семья'?", "Семья на каникулах", "Семья, законно принявшая чужого ребенка на воспитание", "Семья соседей", "Школьный класс", "Семья, законно принявшая чужого ребенка на воспитание", "Усыновление создает юридическую связь между неродными людьми."},
                {"Кто такой 'предок'?", "Потомок в будущем", "Человек, от которого произошел род (дед, прадед)", "Младший брат", "Сосед", "Человек, от которого произошел род (дед, прадед)", "Предки — это те, кто жил до нас в нашем роду."},
                {"Что означает термин 'лишение родительских прав'?", "Запрет на вождение машины", "Юридическое прекращение прав родителей на ребенка", "Запрет на покупку игрушек", "Отмена отпуска", "Юридическое прекращение прав родителей на ребенка", "Крайняя мера при невыполнении родительских обязанностей."},
                {"Кто такой 'патриарх' семьи?", "Самый младший мальчик", "Старейший мужчина, глава рода", "Семейный юрист", "Сосед", "Старейший мужчина, глава рода", "Это авторитетный глава большой семьи."},
                {"Что такое 'наследство'?", "Подарок на день рождения", "Имущество и права, переходящие после смерти владельца", "Семейная поездка", "Школьный аттестат", "Имущество и права, переходящие после смерти владельца", "Передача накопленных благ следующим поколениям."},
                {"Кто такая 'сноха'?", "Жена сына для его отца", "Сестра жены", "Мать мужа", "Дочь брата", "Жена сына для его отца", "Для матери сына она — невестка, для отца — сноха (хотя сейчас часто говорят просто невестка)."},
                {"Что такое 'завещание'?", "Семейная история", "Документ с распоряжениями на случай смерти", "Приглашение на свадьбу", "Свидетельство о рождении", "Документ с распоряжениями на случай смерти", "Юридический акт распределения имущества."},
                {"Кто такой 'опекун'?", "Друг семьи", "Человек, назначенный для заботы о несовершеннолетнем", "Старый родственник", "Врач", "Человек, назначенный для заботы о несовершеннолетнем", "Опекун несет юридическую ответственность за подопечного."},
                {"Что такое 'мезальянс'?", "Вид спорта", "Брак между людьми разных сословий или статусов", "Семейный обед", "Название города", "Брак между людьми разных сословий или статусов", "Обычно подразумевается неравный брак."},
                {"Кто такие 'свояки'?", "Люди, женатые на сестрах", "Братья и сестры", "Враги", "Незнакомцы", "Люди, женатые на сестрах", "Мужья двух сестер по отношению друг к другу — свояки."},
                {"Как называют жену брата?", "Невестка", "Золовка", "Свояченица", "Теща", "Невестка", "Жена брата или сына — это невестка."},
                {"Что такое 'алименты'?", "Семейный питомец", "Денежное содержание, выплачиваемое родителем на ребенка", "Свадебный подарок", "Карманные деньги", "Денежное содержание, выплачиваемое родителем на ребенка", "Выплачиваются после развода для поддержки детей."},
                {"Кто такая 'двоюродная тетя'?", "Ваша любимая тетя", "Сестра вашего отца или матери", "Двоюродная сестра родителя", "Дочь кузена", "Двоюродная сестра родителя", "Родственница более далекой степени родства, чем тетя."},
                {"Что означает термин 'потомок'?", "Ваш прадедушка", "Лицо, происходящее от предка (дети, внуки)", "Ваш начальник", "Ваш коллега", "Лицо, происходящее от предка (дети, внуки)", "Все будущие поколения вашего рода."},
                {"Что такое 'гражданский брак' в обиходном значении?", "Брак в церкви", "Совместное проживание без регистрации в ЗАГСе", "Брак между военными", "Брак с иностранцем", "Совместное проживание без регистрации в ЗАГСе", "Юридически это называется 'сожительство'."}
        };
    }
    private void seedForFamilyRussian(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = RussianFamilyEasy();
        var md = RussianFamilyMedium();
        var h = RussianFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] PortugueseFamilyEasy() {
        return new String[][] {
                {"Minha Árvore Genealógica", "Dominando os termos básicos de parentesco em português"},
                {"Como você chama o irmão do seu pai?", "Tio", "Sobrinho", "Primo", "Cunhado", "Tio", "O irmão do seu pai ou da sua mãe é seu tio."},
                {"A filha da sua irmã é sua...", "Sobrinha", "Neta", "Prima", "Enteada", "Sobrinha", "A filha de um irmão ou irmã é sobrinha; o filho é sobrinho."},
                {"Como se chama a família formada apenas por pais e filhos?", "Família estendida", "Família nuclear", "Família conjunta", "Família simples", "Família nuclear", "A família nuclear é a unidade básica composta por pais e filhos."},
                {"Quando um homem se casa, a mãe da esposa dele é a sua...", "Madrasta", "Madrinha", "Sogra", "Avó", "Sogra", "Em português, os parentes por casamento formam a 'família política'."},
                {"Como você chama o seu filho do sexo masculino?", "Filha", "Filho", "Irmão", "Sobrinho", "Filho", "Um filho é do sexo masculino; uma filha é do sexo feminino."},
                {"Quem é o seu 'primo de primeiro grau'?", "O filho do seu tio ou tia", "O filho do seu irmão", "O irmão do seu pai", "Seu meio-irmão", "O filho do seu tio ou tia", "Os primos de primeiro grau compartilham os mesmos avós."},
                {"Se seu pai se casar novamente, a nova esposa dele é sua...", "Tia", "Sogra", "Madrasta", "Ex-mãe", "Madrasta", "O prefixo 'madr-' ou 'padr-' indica relação por novo casamento."},
                {"Como você chama a pessoa com quem está noivo para casar?", "Esposo", "Noivo / Noiva", "Parceiro", "Parente", "Noivo / Noiva", "É a pessoa com quem você tem um compromisso formal de casamento."},
                {"O que são 'Irmãos'?", "Seus primos", "Seus irmãos ou irmãs", "Apenas seus gêmeos", "Seus pais", "Seus irmãos ou irmãs", "É o termo geral para os filhos dos mesmos pais."},
                {"Como você chama o pai do seu pai?", "Bisavô", "Avô", "Tio", "Padrasto", "Avô", "O avô é o pai de um dos seus progenitores."},
                {"Qual é o feminino de 'Genro' (o marido da sua filha)?", "Genra", "Nora", "Consogra", "Filha", "Nora", "Genro é para o homem, nora é para a mulher."},
                {"O que é um 'Filho único'?", "Uma criança sem irmãos", "O filho mais velho", "O filho mais novo", "Uma criança com um gêmeo", "Uma criança sem irmãos", "Alguém que não tem irmãos nem irmãs."},
                {"Quem é o seu 'Cônjuge'?", "Seu irmão", "Seu marido ou esposa", "Seu pai", "Seu vizinho", "Seu marido ou esposa", "É um termo formal para a pessoa com quem você está casado."},
                {"Como você chama o filho do seu filho?", "Sobrinho", "Bisneto", "Neto", "Primo", "Neto", "O filho do seu filho ou filha é seu neto."},
                {"O que é um 'Meio-irmão'?", "O filho do seu padrasto/madrasta", "Seu irmão biológico", "Seu cunhado", "Seu primo", "O filho do seu padrasto/madrasta", "Irmãos que compartilham apenas um dos pais."},
                {"Como você chama a filha do seu irmão ou irmã?", "Sobrinha", "Sobrinho", "Prima", "Neta", "Sobrinha", "A sobrinha é a filha de um irmão ou irmã."},
                {"Quem são seus 'Parentes'?", "Apenas seus pais", "Todas as pessoas da sua família", "Seus amigos próximos", "Seus vizinhos", "Todas as pessoas da sua família", "Parentes inclui tios, primos, avós, etc."},
                {"O que é uma 'Viúva'?", "Uma mulher cujo marido morreu", "Uma mulher divorciada", "Uma mulher solteira", "Uma mãe de gêmeos", "Uma mulher cujo marido morreu", "Se for homem, chama-se viúvo."},
                {"Como você chama o novo marido da sua mãe que não é seu pai biológico?", "Tio", "Padrasto", "Sogro", "Tutor", "Padrasto", "É seu padrasto através do novo casamento da mãe."},
                {"Como você chama a mãe dos seus filhos?", "Avó", "Tia", "Esposa", "Sobrinha", "Esposa", "Geralmente refere-se à esposa ou companheira."}
        };
    }public static String[][] PortugueseFamilyMedium() {
        return new String[][] {
                {"Vida e Tradições", "Entendendo expressões e costumes sociais lusófonos"},
                {"O que significa o ditado 'Tal pai, tal filho'?", "Os filhos são diferentes", "Os filhos se parecem com os pais", "O pai é alto", "A família é rica", "Os filhos se parecem com os pais", "Significa que os filhos herdam o caráter ou aparência dos pais."},
                {"O que é um 'Padrinho' ou 'Madrinha'?", "Um vizinho", "Um guia escolhido pelos pais no batismo", "Um médico", "O irmão mais velho", "Um guia escolhido pelos pais no batismo", "Têm um papel afetivo e de proteção na vida da criança."},
                {"O que significa ser a 'Ovelha negra' da família?", "O filho favorito", "Aquele que é diferente ou causa problemas", "O membro mais velho", "O mais rico", "Aquele que é diferente ou causa problemas", "Alguém que não segue as normas da família."},
                {"O que é o 'Caçula'?", "O filho mais velho", "O filho mais jovem", "O pai", "O cachorro da família", "O filho mais jovem", "Termo muito usado no Brasil para o último filho a nascer."},
                {"Qual é a tradição do 'Almoço de Domingo'?", "Comer sozinho", "Reunir toda a família para comer", "Ir ao cinema", "Trabalhar", "Reunir toda a família para comer", "É o momento clássico de reunião familiar em Portugal e no Brasil."},
                {"O que significa 'Sangue do meu sangue'?", "Uma ferida", "Alguém da família biológica", "Um amigo", "Um vizinho", "Alguém da família biológica", "Expressão usada para enfatizar laços de parentesco reais."},
                {"Quem são os 'Compadres'?", "Inimigos", "Os pais da criança e os seus padrinhos entre si", "Irmãos", "Avós", "Os pais da criança e os seus padrinhos entre si", "É um vínculo social forte na cultura latina."},
                {"O que significa 'Bater um papo' em família?", "Brigarem", "Conversarem de forma descontraída", "Cozinharem", "Dormirem", "Conversarem de forma descontraída", "Expressão brasileira para uma conversa informal."},
                {"O que é o 'Ninho vazio'?", "Uma casa em obras", "Quando os filhos crescem e saem de casa", "Um pássaro", "Uma casa sem teto", "Quando os filhos crescem e saem de casa", "Refere-se à fase em que os pais ficam sozinhos."},
                {"O que significa 'Lavar roupa suja em público'?", "Limpar a casa", "Discutir problemas familiares na frente de estranhos", "Ir à lavanderia", "Ser fofoqueiro", "Discutir problemas familiares na frente de estranhos", "Diz-se que problemas de família devem ser resolvidos em casa."},
                {"O que é a 'Véspera de Natal'?", "O dia 26 de dezembro", "A noite do dia 24 de dezembro", "O ano novo", "O dia de Reis", "A noite do dia 24 de dezembro", "Principal momento de reunião familiar e ceia."},
                {"O que significa 'Puxar ao pai'?", "Empurrar o pai", "Ter características parecidas com as do pai", "Ajudar o pai", "Caminhar com o pai", "Ter características parecidas com as do pai", "Usado para semelhanças físicas ou de personalidade."},
                {"O que é o 'Sobrenome' ou 'Apelido' (em Portugal)?", "O primeiro nome", "O nome da família (herdado)", "Um pseudônimo", "Uma alcunha", "O nome da família (herdado)", "Em Portugal, 'apelido' significa o que no Brasil chamamos de 'sobrenome'."},
                {"O que se celebra nas 'Bodas de Prata'?", "10 anos de casados", "25 anos de casados", "50 anos de casados", "O noivado", "25 anos de casados", "Prata são 25 anos; Ouro são 50 anos."},
                {"O que significa 'Família de coração'?", "Parentes biológicos", "Pessoas amadas que consideramos família (mesmo sem sangue)", "Médicos", "Vizinhos chatos", "Pessoas amadas que consideramos família (mesmo sem sangue)", "Refere-se a laços de afeto puro."},
                {"O que é o 'Dia dos Pais/Mães'?", "Datas para homenagear os progenitores", "Feriados nacionais", "Eleições", "Festas escolares apenas", "Datas para homenagear os progenitores", "Dias importantes para presentear e reunir a família."},
                {"O que significa 'Dar uma mãozinha' na família?", "Comprar luvas", "Ajudar um parente em alguma tarefa", "Fazer um aceno", "Pedir dinheiro", "Ajudar um parente em alguma tarefa", "Expressão para oferecer ajuda."},
                {"O que é uma 'Alcunha' ou 'Apelido' (no Brasil)?", "Nome oficial", "Um nome carinhoso ou informal (ex: Zé, Gabi)", "O sobrenome", "O título", "Um nome carinhoso ou informal (ex: Zé, Gabi)", "No Brasil, 'apelido' é o nome informal; em Portugal é 'alcunha'."},
                {"O que é 'Estar de castigo'?", "Ganhar um presente", "Sofrer uma punição dos pais por má conduta", "Ir viajar", "Dormir cedo", "Sofrer uma punição dos pais por má conduta", "Comum na infância quando as regras são quebradas."},
                {"O que significa 'Honrar pai e mãe'?", "Desobedecer", "Respeitar e cuidar dos pais", "Morar longe", "Pedir herança", "Respeitar e cuidar dos pais", "Um valor moral e religioso central na família lusófona."}
        };
    }public static String[][] PortugueseFamilyHard() {
        return new String[][] {
                {"Herança e Genealogia", "Explorando relações complexas e termos legais"},
                {"O que é um parente 'Consanguíneo'?", "Parente por afinidade", "Parente de sangue", "Um amigo", "Um vizinho", "Parente de sangue", "Pessoas que partilham o mesmo patrimônio genético."},
                {"Quem é o seu 'Ascendente'?", "Seu filho", "Seu antepassado (pai, avô, etc.)", "Seu irmão", "Seu neto", "Seu antepassado (pai, avô, etc.)", "Pessoas de quem você descende diretamente."},
                {"O que estuda a 'Genealogia'?", "As plantas", "A ascendência e história das famílias", "As rochas", "Os astros", "A ascendência e história das famílias", "A ciência de traçar a árvore familiar."},
                {"O que é 'Parentesco por afinidade'?", "Relação de sangue", "Relação criada pelo casamento (sogros, cunhados)", "Amizade próxima", "Colegas de trabalho", "Relação criada pelo casamento (sogros, cunhados)", "São os parentes do cônjuge."},
                {"O que significa 'Pátrio Poder' (ou Poder Familiar)?", "Um país", "Direitos e deveres dos pais sobre os filhos menores", "Uma festa", "Herança", "Direitos e deveres dos pais sobre os filhos menores", "Conceito jurídico de proteção e autoridade."},
                {"Quem é o 'Primogênito'?", "O filho que nasceu primeiro", "O filho mais novo", "O filho do meio", "O pai", "O filho que nasceu primeiro", "O primeiro filho de um casal."},
                {"O que é o 'Patrimônio' familiar?", "Apenas fotos", "O conjunto de bens e propriedades da família", "O nome da família", "Os amigos", "O conjunto de bens e propriedades da família", "Tudo o que a família possui financeiramente."},
                {"O que é uma 'Linhagem'?", "Uma linha de costura", "A sucessão de gerações de uma família", "A riqueza", "O endereço", "A sucessão de gerações de uma família", "Série de gerações que vêm de um tronco comum."},
                {"Quem é o 'Patriarca'?", "O filho mais novo", "O homem mais velho ou chefe da família", "Um juiz", "Um vizinho", "O homem mais velho ou chefe da família", "A figura masculina central de autoridade."},
                {"O que é uma 'Herança'?", "Um presente de Natal", "Bens deixados por alguém após a morte", "Um emprego", "Um conselho", "Bens deixados por alguém após a morte", "Patrimônio transmitido aos herdeiros."},
                {"O que é o 'Sobrenome Composto'?", "Um nome falso", "Ter dois ou mais sobrenomes (ex: Silva Santos)", "Não ter sobrenome", "Um apelido", "Ter dois ou mais sobrenomes (ex: Silva Santos)", "Muito comum em Portugal e no Brasil ter sobrenomes do pai e da mãe."},
                {"O que é um 'Testamento'?", "Uma carta de amor", "Documento legal que define o destino dos bens após a morte", "Um convite", "Um certidão", "Documento legal que define o destino dos bens após a morte", "Expressão da última vontade de uma pessoa."},
                {"O que é uma 'Família Recomposta'?", "Uma família rica", "Família formada por casais com filhos de uniões anteriores", "Família com gêmeos", "Família que mora no campo", "Família formada por casais com filhos de uniões anteriores", "Conhecida também como 'família mosaico'."},
                {"O que significa 'Emancipação'?", "Casar-se", "Cessação da incapacidade civil do menor (tornar-se adulto legalmente)", "Mudar de casa", "Trabalhar", "Cessação da incapacidade civil do menor (tornar-se adulto legalmente)", "O jovem passa a responder por si antes dos 18 anos."},
                {"O que é a 'Guarda Compartilhada'?", "Uma prisão", "Divisão de responsabilidades sobre o filho entre pais separados", "Um colégio", "Uma viagem", "Divisão de responsabilidades sobre o filho entre pais separados", "Decidida legalmente visando o bem do menor."},
                {"Quem é o 'Tutor'?", "Um professor de música", "Pessoa que protege legalmente um menor ou incapaz", "Um primo", "Um amigo", "Pessoa que protege legalmente um menor ou incapaz", "Assume a responsabilidade legal na falta dos pais."},
                {"O que significa 'Hereditariedade'?", "Gastar dinheiro", "Transmissão de características genéticas dos pais para os filhos", "Viajar muito", "Estudar", "Transmissão de características genéticas dos pais para os filhos", "Explica a semelhança biológica entre gerações."},
                {"O que é a 'Pensão Alimentícia'?", "Comida de graça", "Valor pago para o sustento dos filhos após separação", "Um presente", "Poupança", "Valor pago para o sustento dos filhos após separação", "Obrigação legal de um dos progenitores."},
                {"O que significa 'Berço' em sentido figurado?", "Uma cama de bebê", "A origem familiar ou social de alguém", "A escola", "O hospital", "A origem familiar ou social de alguém", "Exemplo: 'Ele tem bom berço' (foi bem educado pela família)."},
                {"Quem é a 'Tia-avó'?", "Sua tia favorita", "A irmã de um dos seus avós", "A esposa do seu tio", "A mãe do seu primo", "A irmã de um dos seus avós", "Um grau de parentesco na geração dos seus avós."}
        };
    }
    private void seedForFamilyPortuguese(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = PortugueseFamilyEasy();
        var md = PortugueseFamilyMedium();
        var h = PortugueseFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ArabicFamilyEasy() {
        return new String[][] {
                {"عائلتي", "تعلم مصطلحات العائلة الأساسية باللغة العربية"},
                {"ماذا تسمي أخ الأب؟", "العم", "الخال", "الجد", "ابن العم", "العم", "أخ الأب هو العم، بينما أخ الأم هو الخال."},
                {"ماذا تسمي أخت الأم؟", "العمة", "الخالة", "الجدة", "بنت الخالة", "الخالة", "أخت الأم هي الخالة، وأخت الأب هي العمة."},
                {"من هو والد الوالد؟", "الجد", "العم", "الخال", "الحفيد", "الجد", "والد الأب أو والد الأم يسمى الجد."},
                {"ماذا تسمى ابنة الأخ أو الأخت؟", "ابنة الأخ/الأخت", "الحفيدة", "العمة", "الخالة", "ابنة الأخ/الأخت", "يستخدم مصطلح 'ابنة أخي' أو 'ابنة أختي'."},
                {"من هو ابن العم؟", "ابن أخ الأب", "ابن أخ الأم", "ابن الأخت", "ابن الأخ", "ابن أخ الأب", "في الثقافة العربية، أبناء الأعمام هم أقارب مقربون جداً."},
                {"ماذا تسمى زوجة الأب (ليست الأم البيولوجية)؟", "خالتي", "زوجة الأب", "عمتي", "الجدة", "زوجة الأب", "تستخدم للإشارة إلى زوجة الأب في حال زواجه مرة أخرى."},
                {"ماذا تسمى والدة الزوج أو الزوجة؟", "الحماة", "الخالة", "العمة", "الجدة", "الحماة", "الحماة هي والدة الشريك بعد الزواج."},
                {"ماذا يسمى الرجل الذي تزوج ابنتك؟", "الصهر (الزوج)", "الابن", "الحفيد", "ابن الأخ", "الصهر (الزوج)", "يطلق عليه الصهر أو زوج الابنة."},
                {"ماذا يسمى الشخص الذي ليس له إخوة؟", "الابن الوحيد", "البكر", "الأصغر", "اليتيم", "الابن الوحيد", "هو الطفل الذي لا يملك إخوة أو أخوات."},
                {"ماذا تسمى المرأة التي توفي زوجها؟", "أرملة", "مطلقة", "عزباء", "عروس", "أرملة", "الرجل الذي توفيت زوجته يسمى أرمل."},
                {"ماذا تسمي والدة والدتك؟", "الجدة", "الخالة", "العمة", "الحماة", "الجدة", "الجدة هي أم الأب أو أم الأم."},
                {"من هم 'الأشقاء'؟", "الإخوة من نفس الأب والأم", "الأقارب", "الأصدقاء", "الجيران", "الإخوة من نفس الأب والأم", "يطلق عليهم الإخوة الأشقاء."},
                {"ماذا يسمى الطفل الذي فقد والديه؟", "يتيم", "وحيد", "بكر", "حفيد", "يتيم", "اليتيم هو من فقد الأب أو كلا الوالدين في سن صغيرة."},
                {"ماذا يسمى أخ الزوج أو أخ الزوجة؟", "نسيب", "سلف", "ابن عم", "خال", "نسيب", "يستخدم مصطلح 'نسيبي' للإشارة إلى أقارب الزوج/الزوجة."},
                {"ماذا تسمى ابنة الابن أو ابنة الابنة؟", "الحفيدة", "الابنة", "بنت الأخ", "بنت الأخت", "الحفيدة", "أبناء الأبناء هم الأحفاد."},
                {"ماذا تسمى الأخت الكبرى في بعض اللهجات احتراما؟", "أختي", "أبلة / ست", "خالة", "عمة", "أبلة / ست", "تستخدم بعض الكلمات لإظهار الاحترام للأخت الكبرى."},
                {"ماذا تسمى زوجة الابن؟", "الكنة", "الابنة", "الأخت", "الحفيدة", "الكنة", "الكنة هي زوجة الابن بالنسبة لوالديه."},
                {"ماذا يسمى المولود الأول للعائلة؟", "البكر", "الأصغر", "الوحيد", "الحفيد", "البكر", "الابن البكر عادة ما يكون له مكانة خاصة."},
                {"ماذا تسمى عائلة الزوج بالنسبة للزوجة؟", "أهل الزوج", "الأصدقاء", "الجيران", "الغرباء", "أهل الزوج", "العلاقة مع أهل الزوج مهمة جداً في الثقافة العربية."},
                {"ماذا يسمى والد الزوج أو الزوجة؟", "الحمو", "الخال", "العم", "الجد", "الحمو", "الحمو هو والد الشريك."}
        };
    }public static String[][] ArabicFamilyHard() {
        return new String[][] {
                {"الأنساب والمواريث", "استكشاف العلاقات المعقدة والمصطلحات القانونية العائلية"},
                {"ما هو 'علم الأنساب'؟", "علم دراسة أصول العائلات وترتيبها", "علم النبات", "علم الفضاء", "علم الكيمياء", "علم دراسة أصول العائلات وترتيبها", "برع العرب قديماً في حفظ أنسابهم."},
                {"ماذا يعني 'الوريث الشرعي'؟", "الشخص الذي يحق له نيل الميراث قانوناً", "الصديق", "الجار", "المشتري", "الشخص الذي يحق له نيل الميراث قانوناً", "يتم تحديده بناءً على درجات القرابة."},
                {"ما هو 'شجر العائلة'؟", "رسم تخطيطي يوضح تسلسل الأجيال", "شجرة في الحديقة", "نوع من الفاكهة", "خريطة طريق", "رسم تخطيطي يوضح تسلسل الأجيال", "يستخدم لتتبع الأجداد والأحفاد."},
                {"ما معنى 'الأقربون أولى بالمعروف'؟", "الأهل أحق بالمساعدة والصدقة", "الغرباء أولى", "لا أحد يستحق", "الأصدقاء أولى", "الأهل أحق بالمساعدة والصدقة", "مبدأ اجتماعي يعزز التكافل العائلي."},
                {"ما هو 'الميراث'؟", "ما يتركه المتوفى من مال أو عقار لورثته", "هدية نجاح", "قرض", "راتب شهري", "ما يتركه المتوفى من مال أو عقار لورثته", "توزع وفق أحكام دقيقة."},
                {"ماذا يعني مصطلح 'الخلف'؟", "الأبناء والأحفاد (الأجيال القادمة)", "الأجداد", "الإخوة", "الأصدقاء", "الأبناء والأحفاد (الأجيال القادمة)", "يقال 'خير خلف لخير سلف'."},
                {"ما هي 'الوصية'؟", "وثيقة تحدد رغبات الشخص بعد موته", "رسالة حب", "دعوة عرس", "شهادة ميلاد", "وثيقة تحدد رغبات الشخص بعد موته", "تنفذ في حدود الثلث من المال."},
                {"ماذا يسمى الزواج من خارج القبيلة أو العائلة؟", "زواج الأغارب", "زواج الأقارب", "عزوبية", "خطوبة", "زواج الأغارب", "يعتبر وسيلة لتقوية الروابط بين المجموعات المختلفة."},
                {"ما معنى 'التبني' في المفهوم القانوني العربي العام؟", "رعاية اليتيم (كفالة) دون تغيير نسبه", "تغيير اسم الطفل", "بيعه", "تركه", "رعاية اليتيم (كفالة) دون تغيير نسبه", "الكفالة هي المصطلح المعتمد بدلاً من التبني الكامل الذي يغير النسب."},
                {"ما هو 'عميد الأسرة'؟", "أكبر الأعضاء سناً وأكثرهم حكمة", "أصغر طفل", "الخادم", "الجار", "أكبر الأعضاء سناً وأكثرهم حكمة", "هو المرجع في القرارات الكبيرة."},
                {"ما معنى 'ابن السبيل' في السياق العائلي القديم؟", "المسافر المنقطع عن أهله", "الابن الضائع", "ابن الأخ", "الحفيد", "المسافر المنقطع عن أهله", "كان يحظى برعاية العائلات كأنه فرد منها."},
                {"ماذا يسمى 'ابن ابن الابن'؟", "ابن الحفيد", "الحفيد", "الابن", "الجد", "ابن الحفيد", "درجة بعيدة من الأحفاد."},
                {"ما معنى 'قطع الأرحام'؟", "هجر الأقارب وعدم التواصل معهم", "السفر", "العمل معهم", "حبهم", "هجر الأقارب وعدم التواصل معهم", "تعتبر من الأمور المنبوذة اجتماعياً."},
                {"ما هو 'دفتر العائلة'؟", "وثيقة رسمية تسجل أفراد الأسرة", "كراسة رسم", "كتاب طبخ", "مذكرة", "وثيقة رسمية تسجل أفراد الأسرة", "مهم جداً للمعاملات الحكومية في الدول العربية."},
                {"ماذا يسمى الشخص الذي يفتخر بأجداده كثيراً؟", "مفاخر بنسبه", "متواضع", "جاهل", "فقير", "مفاخر بنسبه", "صفة شائعة في الشعر العربي القديم."},
                {"ماذا يعني 'عقوق الوالدين'؟", "الإساءة إليهما وعصيانهما", "الإحسان إليهما", "زيارتهما", "الصلاة لهما", "الإساءة إليهما وعصيانهما", "تعتبر من أكبر الكبائر الأخلاقية."},
                {"ماذا يسمى 'الجد الأكبر' الذي بدأت منه العائلة؟", "الجد الجامع / السلف", "الحفيد", "العم", "الخال", "الجد الجامع / السلف", "هو أصل الشجرة العائلية."},
                {"ما معنى 'الحضانة'؟", "حق رعاية الطفل وتربيته بعد الانفصال", "المدرسة", "النوم", "اللعب", "حق رعاية الطفل وتربيته بعد الانفصال", "تخضع لقوانين الأحوال الشخصية."},
                {"ماذا يعني مصطلح 'الأقارب من جهة العصب'؟", "الأقارب من جهة الأب", "الأقارب من جهة الأم", "الجيران", "الأصدقاء", "الأقارب من جهة الأب", "مصطلح فقهي يستخدم في المواريث."},
                {"ماذا تسمى العائلة الممتدة التي تشترك في جد واحد بعيد؟", "الحمولة / العشيرة", "الجيران", "النادي", "الشركة", "الحمولة / العشيرة", "تمثل الوحدة الاجتماعية الأكبر في القرى والبوادي."}
        };
    }public static String[][] ArabicFamilyMedium() {
        return new String[][] {
            {"القيم والتقاليد", "فهم التقاليد العائلية العربية والقيم الاجتماعية"},
            {"ماذا يعني مصطلح 'صلة الرحم'؟", "زيارة الأقارب وبرهم", "قطع العلاقات", "السفر", "التجارة", "زيارة الأقارب وبرهم", "تعتبر صلة الرحم من أهم الواجبات الاجتماعية والدينية."},
            {"ما هو أهم اجتماع عائلي في الأسبوع؟", "يوم الجمعة", "يوم الاثنين", "يوم الأربعاء", "يوم السبت", "يوم الجمعة", "غالباً ما تجتمع العائلة الكبيرة على الغداء بعد صلاة الجمعة."},
            {"ماذا نفعل لإظهار الاحترام لكبار السن في العائلة؟", "تقبيل اليد أو الرأس", "تجاهلهم", "مقاطعتهم", "الجلوس قبلهم", "تقبيل اليد أو الرأس", "هو تعبير تقليدي عن التقدير والاحترام."},
            {"ماذا يسمى المال الذي يعطى للأطفال في العيد؟", "العيدية", "الراتب", "الدين", "الصدقة", "العيدية", "عادة عربية لإدخال البهجة على قلوب الأطفال في الأعياد."},
            {"ما هو 'بيت العيلة'؟", "بيت الجد والجدة حيث يجتمع الجميع", "المستشفى", "المدرسة", "المكتب", "بيت الجد والجدة حيث يجتمع الجميع", "هو رمز للوحدة والترابط العائلي."},
            {"ماذا يعني المثل 'الخال والد'؟", "الخال له مكانة الأب", "الخال غريب", "الخال عدو", "الخال صغير", "الخال له مكانة الأب", "يعبر عن المحبة والتقدير الكبير للخال في الثقافة."},
            {"ما هو 'المهر'؟", "هدية الزوج لزوجته عند الزواج", "قرض بنكي", "ثمن طعام", "تذكرة سفر", "هدية الزوج لزوجته عند الزواج", "هو حق شرعي للمرأة في الزواج."},
            {"ماذا يسمى الاحتفال بمرور 7 أيام على ولادة الطفل؟", "العقيقة / السبوع", "عيد ميلاد", "تخرج", "خطوبة", "العقيقة / السبوع", "احتفال تقليدي بذبح شاة وتوزيع الطعام."},
            {"ما معنى 'عزوة'؟", "الأهل والأقارب الذين يدعمون الشخص", "الوحدة", "السفر", "المال", "الأهل والأقارب الذين يدعمون الشخص", "تعني السند والقوة المستمدة من كثرة الأهل والولاء."},
            {"ماذا يقول الأبناء لوالديهم تعبيراً عن الطاعة؟", "سمعاً وطاعة / حاضر", "لا", "لماذا", "بعد قليل", "سمعاً وطاعة / حاضر", "تعبيراً عن 'بر الوالدين'."},
            {"كيف ينادى الأب في الكثير من المناطق العربية؟", "يابا / أبوي", "سيدي", "أستاذ", "يا رجل", "يابا / أبوي", "تختلف اللهجات ولكن المعنى واحد وهو التقدير."},
            {"ما هو 'النسب'؟", "الارتباط بين عائلتين عن طريق الزواج", "الاسم الأول", "المهنة", "العمر", "الارتباط بين عائلتين عن طريق الزواج", "المصاهرة والنسب يقويان الروابط الاجتماعية بين القبائل والعائلات."},
            {"ماذا تعني 'بر الوالدين'؟", "الإحسان إليهما وطاعتهما", "إهمالهما", "السفر بعيداً عنهما", "العمل معهما", "الإحسان إليهما وطاعتهما", "قيمة أساسية في التربية العربية."},
            {"ماذا تسمى وليمة العرس؟", "العشاء", "الوليمة / القرى", "الفطور", "الغداء", "الوليمة / القرى", "إطعام الضيوف جزء أساسي من كرم الضيافة في الأفراح."},
            {"ما معنى 'ابن أصول'؟", "شخص من عائلة كريمة وذو أخلاق", "شخص غني", "شخص متعلم", "شخص يسافر كثيراً", "شخص من عائلة كريمة وذو أخلاق", "تعبير يستخدم للمدح."},
            {"ما هو 'اللقب' أو 'الكنية'؟", "اسم العائلة", "الاسم الأول", "تاريخ الميلاد", "مكان العمل", "اسم العائلة", "يعتز العرب كثيراً بألقاب عائلاتهم وتاريخها."},
            {"ماذا يسمى الشخص الذي يزور أقاربه باستمرار؟", "واصل للرحم", "قاطع للرحم", "مسافر", "تاجر", "واصل للرحم", "صفة ممدوحة جداً."},
            {"ماذا تعني كلمة 'عيلة'؟", "الأسرة الصغيرة والكبيرة", "العمل", "الدراسة", "الرياضة", "الأسرة الصغيرة والكبيرة", "تستخدم للدلالة على الترابط."},
            {"ما هو دور 'كبير العائلة'؟", "حل النزاعات وتقديم الحكمة", "اللعب", "النوم", "التسوق", "حل النزاعات وتقديم الحكمة", "كلمته مسموعة ومحترمة من الجميع."},
            {"ماذا يرتدي الناس غالباً في تجمعات العيد العائلية؟", "الملابس الجديدة والتقليدية", "ملابس الرياضة", "ملابس النوم", "ملابس العمل", "الملابس الجديدة والتقليدية", "تعبيراً عن الفرح والزينة."}
        };
    }
    private void seedForFamilyArabic(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = ArabicFamilyEasy();
        var md = ArabicFamilyMedium();
        var h = ArabicFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ItalianFamilyEasy() {
        return new String[][] {
                {"La Mia Famiglia", "Imparare i termini di parentela di base in italiano"},
                {"Come chiami il fratello di tuo padre?", "Lo zio", "Il nipote", "Il cugino", "Il cognato", "Lo zio", "Il fratello del padre o della madre è lo zio."},
                {"La figlia di tua sorella è la tua...", "Nipote", "Figlia", "Cugina", "Sorellastra", "Nipote", "In italiano, 'nipote' si usa sia per i figli dei fratelli che per i figli dei figli."},
                {"Come si chiama la madre di tua moglie?", "La suocera", "La madrina", "La zia", "La nonna", "La suocera", "I parenti acquisiti tramite il matrimonio formano la famiglia 'legale'."},
                {"Chi è il figlio di tuo zio?", "Tuo cugino", "Tuo fratello", "Tuo nipote", "Tuo nonno", "Tuo cugino", "I figli degli zii sono i cugini."},
                {"Come chiami tuo figlio maschio?", "Figlio", "Fratello", "Nipote", "Padre", "Figlio", "Il figlio è il discendente diretto maschio."},
                {"Se tuo padre si risposa, la sua nuova moglie è la tua...", "Madrastra", "Suocera", "Zia", "Nonna", "Madrastra", "Anche se oggi si usa spesso 'la moglie di papà', il termine corretto è madrastra."},
                {"Come si chiama il padre di tuo padre?", "Il nonno", "Il bisnonno", "Lo zio", "Il suocero", "Il nonno", "I nonni sono le radici della famiglia italiana."},
                {"Qual è il femminile di 'Genero' (il marito di tua figlia)?", "Nuora", "Genera", "Sorella", "Cugina", "Nuora", "Il marito della figlia è il genero, la moglie del figlio è la nuora."},
                {"Chi sono i 'Fratelli'?", "I figli degli stessi genitori", "I cugini", "I nonni", "Gli zii", "I figli degli stessi genitori", "Include sia maschi che femmine (plurale maschile)."},
                {"Come chiami la sorella di tua madre?", "La zia", "La nonna", "La cugina", "La nipote", "La zia", "La sorella della mamma è la zia."},
                {"Cosa significa 'Figlio unico'?", "Un bambino senza fratelli o sorelle", "Il primo figlio", "L'ultimo figlio", "Un gemello", "Un bambino senza fratelli o sorelle", "Una persona che non ha fratelli."},
                {"Chi è il 'Coniuge'?", "Il marito o la moglie", "Il fratello", "Il cugino", "Il vicino", "Il marito o la moglie", "Termine formale per indicare la persona sposata."},
                {"Come chiami il figlio di tuo figlio?", "Nipote", "Bisnipote", "Cugino", "Zio", "Nipote", "In italiano 'nipote' indica sia 'grandchild' che 'nephew/niece'."},
                {"Chi è la 'Mamma'?", "La madre", "La nonna", "La zia", "La sorella", "La madre", "Mamma è il termine affettuoso usato universalmente in Italia."},
                {"Come si chiama il marito di tua sorella?", "Cognato", "Suocero", "Zio", "Fratello", "Cognato", "Il cognato è il fratello del coniuge o il marito della sorella."},
                {"Chi è la 'Sorella'?", "La figlia dei tuoi genitori", "La figlia di tua zia", "Tua madre", "Tua nonna", "La figlia dei tuoi genitori", "La parente stretta di sesso femminile."},
                {"Cosa sono i 'Parenti'?", "Tutti i membri della famiglia", "Solo i genitori", "Gli amici", "I compagni di classe", "Tutti i membri della famiglia", "Attenzione: in inglese 'parents' significa genitori, in italiano 'parenti' significa relatives."},
                {"Chi è la 'Bisnonna'?", "La madre di tua nonna", "La sorella di tua madre", "La figlia di tua figlia", "Tua suocera", "La madre di tua nonna", "La generazione precedente ai nonni."},
                {"Come chiami il fratello di tua moglie?", "Cognato", "Suocero", "Zio", "Cugino", "Cognato", "È tuo cognato per via del matrimonio."},
                {"Chi è il 'Papà'?", "Il padre", "Il nonno", "Lo zio", "Il figlio", "Il padre", "Papà o babbo (in Toscana) sono i nomi affettuosi per il padre."}
        };
    }public static String[][] ItalianFamilyMedium() {
        return new String[][] {
                {"Vita e Tradizioni Italiane", "Capire i proverbi e le abitudini sociali della famiglia"},
                {"Cosa significa 'Tale padre, tale figlio'?", "I figli somigliano ai genitori", "I figli sono diversi", "Il padre è alto", "La famiglia è povera", "I figli somigliano ai genitori", "Significa che i figli ereditano i difetti o i pregi dei genitori."},
                {"Cos'è il 'Pranzo della Domenica'?", "Un pasto veloce", "Una riunione di tutta la famiglia", "Un pasto al ristorante", "Un digiuno", "Una riunione di tutta la famiglia", "È la tradizione sacra di riunirsi dai nonni per mangiare insieme."},
                {"Chi è il 'Padrino'?", "Un vicino", "Una guida spirituale scelta per il Battesimo", "Un medico", "Un attore", "Una guida spirituale scelta per il Battesimo", "Ha un ruolo importante di protezione e affetto."},
                {"Cosa significa 'I panni sporchi si lavano in famiglia'?", "Bisogna fare il bucato", "I problemi familiari vanno risolti in privato", "Bisogna parlare con i vicini", "La casa è sporca", "I problemi familiari vanno risolti in privato", "Le questioni private non devono essere discusse fuori casa."},
                {"Chi è la 'Pecora nera'?", "Il figlio preferito", "Il membro della famiglia che si comporta male o è diverso", "Un animale", "Il nonno", "Il membro della famiglia che si comporta male o è diverso", "Qualcuno che non segue le regole o le tradizioni familiari."},
                {"Cosa significa 'Avere un debole' per un nipote?", "Essere malati", "Avere una preferenza affettuosa", "Essere arrabbiati", "Non vederlo mai", "Avere una preferenza affettuosa", "Spesso i nonni 'hanno un debole' per i nipoti più piccoli."},
                {"Cosa significa l'espressione 'Mamma mia!'?", "Un richiamo alla madre", "Un'esclamazione di sorpresa o shock", "Un saluto", "Una domanda", "Un'esclamazione di sorpresa o shock", "È l'espressione italiana più famosa al mondo."},
                {"In Italia, cosa si festeggia il 19 marzo?", "La festa della mamma", "La festa del papà (San Giuseppe)", "Natale", "Pasqua", "La festa del papà (San Giuseppe)", "Si festeggia nel giorno di San Giuseppe con dolci tipici come le zeppole."},
                {"Chi sono i 'Parenti serpenti'?", "Parenti che amano gli animali", "Parenti falsi o che litigano tra loro", "Parenti molto agili", "Parenti che vivono in campagna", "Parenti falsi o che litigano tra loro", "Espressione usata per indicare tensioni nascoste in famiglia."},
                {"Cosa si lancia agli sposi fuori dalla chiesa?", "Acqua", "Riso", "Pane", "Fiori", "Riso", "È un augurio di fertilità e abbondanza."},
                {"Cosa sono i 'Confetti'?", "Piccoli pezzi di carta", "Mandorle ricoperte di zucchero per le cerimonie", "Frutta", "Giocattoli", "Mandorle ricoperte di zucchero per le cerimonie", "Si regalano in piccoli sacchetti (bomboniere) ai matrimoni o battesimi."},
                {"Chi è il 'Mammone'?", "Un uomo molto forte", "Un uomo adulto troppo attaccato alla madre", "Un bambino piccolo", "Un padre severo", "Un uomo adulto troppo attaccato alla madre", "Stereotipo comune dell'uomo italiano che non vuole lasciare la casa materna."},
                {"Cosa significa 'Casa dolce casa'?", "La casa è fatta di zucchero", "Il piacere di stare nel proprio ambiente familiare", "Bisogna pulire casa", "La casa è piccola", "Il piacere di stare nel proprio ambiente familiare", "Riflette l'importanza del focolare domestico."},
                {"Cosa si fa per il 'Cenone di Natale'?", "Si mangia molto con la famiglia", "Si va a dormire presto", "Si lavora", "Si va in palestra", "Si mangia molto con la famiglia", "La vigilia di Natale è un momento di grande unione familiare."},
                {"Qual è il ruolo della 'Nonna' in cucina?", "Mangiare fuori", "Tramandare le ricette segrete di famiglia", "Comprare cibo pronto", "Non cucinare", "Tramandare le ricette segrete di famiglia", "Le nonne sono le custodi della tradizione culinaria italiana."},
                {"Cosa significa 'Essere il cocco di mamma'?", "Essere il figlio preferito", "Essere un frutto", "Essere cattivi", "Essere il più alto", "Essere il figlio preferito", "Il figlio che riceve più attenzioni e vizi."},
                {"Cosa si intende per 'Famiglia allargata'?", "Una famiglia che mangia molto", "Una famiglia con genitori, figli di unioni diverse e parenti vari", "Una famiglia che vive in una casa grande", "Solo i genitori", "Una famiglia con genitori, figli di unioni diverse e parenti vari", "Modello familiare sempre più comune."},
                {"Cosa si festeggia l'8 dicembre in famiglia?", "L'Immacolata Concezione (spesso si fa l'albero)", "Il compleanno del nonno", "La fine della scuola", "L'estate", "L'Immacolata Concezione (spesso si fa l'albero)", "Tradizionalmente si inizia ad addobbare la casa per Natale."},
                {"Cosa significa l'augurio 'Auguri e figli maschi'?", "Un augurio tradizionale (ma datato) di prosperità per gli sposi", "Un insulto", "Un consiglio medico", "Un invito a cena", "Un augurio tradizionale (ma datato) di prosperità per gli sposi", "Si diceva un tempo ai matrimoni."},
                {"Cosa significa 'Mettere su famiglia'?", "Costruire una casa", "Sposarsi e avere figli", "Comprare mobili", "Andare a vivere da soli", "Sposarsi e avere figli", "Iniziare il proprio percorso familiare indipendente."}
        };
    }public static String[][] ItalianFamilyHard() {
        return new String[][] {
                {"Eredità e Genealogia", "Esplorare i legami complessi e i termini legali della famiglia"},
                {"Cos'è l'Albero Genealogico?", "Una pianta da giardino", "La rappresentazione grafica dei legami di parentela", "Un libro di cucina", "Una mappa stradale", "La rappresentazione grafica dei legami di parentela", "Serve a ricostruire la storia degli antenati."},
                {"Cosa significa 'Consanguineo'?", "Parente legato da vincoli di sangue", "Parente acquisito", "Un amico intimo", "Un collega", "Parente legato da vincoli di sangue", "Indica la discendenza biologica dallo stesso stipite."},
                {"Chi è l'Erede?", "Chi deve pagare i debiti", "Chi riceve i beni dopo la morte di un parente", "Il figlio maggiore", "Un vicino", "Chi riceve i beni dopo la morte di un parente", "Colui che succede nei diritti patrimoniali."},
                {"Cos'è la 'Successione'?", "Una serie di eventi", "Il passaggio dei beni dal defunto ai superstiti", "Un successo lavorativo", "Un matrimonio", "Il passaggio dei beni dal defunto ai superstiti", "Procedura legale che regola l'eredità."},
                {"Cosa si intende per 'Patria Potestà' (oggi Responsabilità Genitoriale)?", "Il potere del governo", "L'insieme dei doveri e diritti dei genitori sui figli minori", "Una festa nazionale", "Un tipo di tassa", "L'insieme dei doveri e diritti dei genitori sui figli minori", "Garantisce la protezione e l'educazione dei figli."},
                {"Chi sono gli 'Antenati'?", "I figli futuri", "I predecessori della famiglia (bisnonni, trisnonni)", "I fratelli", "I vicini", "I predecessori della famiglia (bisnonni, trisnonni)", "Coloro dai quali discende la stirpe."},
                {"Cos'è un 'Testamento'?", "Una promessa verbale", "Un atto legale con cui si dispone dei propri beni per il futuro", "Un certificato di nascita", "Un invito", "Un atto legale con cui si dispone dei propri beni per il futuro", "Fondamentale per decidere come dividere l'eredità."},
                {"Cosa significa 'Agnazione'?", "Parentela solo per via materna", "Parentela solo per via paterna", "Amicizia", "Adozione", "Parentela solo per via paterna", "Termine storico e giuridico legato alla linea maschile."},
                {"Chi è il 'Patriarca'?", "Il nipote", "Il capo o il membro più anziano di una famiglia", "Un avvocato", "Un bambino", "Il capo o il membro più anziano di una famiglia", "Figura di riferimento e autorità nel clan familiare."},
                {"Cosa si intende per 'Stato di Famiglia'?", "L'umore dei genitori", "Un documento ufficiale che elenca i membri conviventi", "La ricchezza della famiglia", "La nazione", "Un documento ufficiale che elenca i membri conviventi", "Certificato anagrafico rilasciato dal Comune."},
                {"Cosa significa 'Parentela di primo grado'?", "Genitori e figli", "Cugini", "Zii", "Amici", "Genitori e figli", "Il legame più diretto e stretto."},
                {"Cosa sono le 'Pubblicazioni' di matrimonio?", "Libri di famiglia", "Avvisi ufficiali affissi in Comune prima delle nozze", "Foto del matrimonio", "Regali", "Avvisi ufficiali affissi in Comune prima delle nozze", "Servono a rendere pubblico il futuro matrimonio per eventuali opposizioni."},
                {"Chi è il 'Tutore'?", "Un insegnante", "Persona nominata per proteggere un minore o un incapace", "Un cugino", "Un testimone", "Persona nominata per proteggere un minore o un incapace", "Agisce legalmente per conto di chi non può farlo."},
                {"Cosa significa 'Affinità' in termini legali?", "Simpatia tra amici", "Il vincolo che unisce un coniuge ai parenti dell'altro", "Hobby comuni", "Hanno lo stesso sangue", "Il vincolo che unisce un coniuge ai parenti dell'altro", "Ad esempio il rapporto tra nuora e suocera."},
                {"Cos'è l' 'Affido Familiare'?", "Un'adozione definitiva", "L'accoglienza temporanea di un minore in difficoltà", "Un collegio", "Una vacanza", "L'accoglienza temporanea di un minore in difficoltà", "Misura di protezione temporanea per il bambino."},
                {"Cosa significa 'Legittima'?", "Una cosa giusta", "La quota di eredità che spetta obbligatoriamente ai parenti stretti", "Una legge dello Stato", "Un tipo di firma", "La quota di eredità che spetta obbligatoriamente ai parenti stretti", "I figli e il coniuge hanno diritto a una parte minima dei beni per legge."},
                {"Cosa si intende per 'Separazione dei beni'?", "Vivere in case diverse", "Regime patrimoniale dove ogni coniuge resta proprietario dei suoi acquisti", "Litigare per i mobili", "Dividere i vestiti", "Regime patrimoniale dove ogni coniuge resta proprietario dei suoi acquisti", "Opposto alla 'comunione dei beni'."},
                {"Chi è il 'Capofamiglia'?", "Chi corre più veloce", "Colui che rappresenta o guida l'unità familiare", "Il cane", "L'ospite", "Colui che rappresenta o guida l'unità familiare", "Termine tradizionale per chi ha la responsabilità principale."},
                {"Cos'è il 'Matrimonio Concordatario'?", "Un matrimonio civile", "Un matrimonio religioso con effetti civili", "Un accordo tra amici", "Un tipo di ballo", "Un matrimonio religioso con effetti civili", "Celebrato in chiesa e trascritto nei registri dello Stato."},
                {"Cosa significa 'Stirpe'?", "Pulire", "Discendenza, famiglia nel senso storico e nobile", "Un tipo di abito", "Una strada", "Discendenza, famiglia nel senso storico e nobile", "Indica le origini profonde di un casato."}
        };
    }
    private void seedForFamilyItalian(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = ItalianFamilyEasy();
        var md = ItalianFamilyMedium();
        var h = ItalianFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] TurkishFamilyEasy() {
        return new String[][] {
                {"Ailem", "Temel aile üyelerini ve akrabalık terimlerini öğrenin"},
                {"Babanızın erkek kardeşine ne denir?", "Amca", "Dayı", "Hala", "Teyze", "Amca", "Babanın erkek kardeşi amca, annenin erkek kardeşi dayıdır."},
                {"Annenizin kız kardeşine ne denir?", "Hala", "Teyze", "Yenge", "Görümce", "Teyze", "Annenin kız kardeşi teyze, babanın kız kardeşi haladır."},
                {"Babanızın babasına ne denir?", "Dede", "Babaanne", "Anneanne", "Amca", "Dede", "Hem anne hem de baba tarafındaki büyükbabalara dede denir."},
                {"Babanızın annesine ne denir?", "Anneanne", "Babaanne", "Hala", "Teyze", "Babaanne", "Babanın annesi babaanne, annenin annesi anneannedir."},
                {"Kardeşinizin kız çocuğuna ne denir?", "Yeğen", "Kuzen", "Torun", "Gelin", "Yeğen", "Kardeş çocuklarına cinsiyet fark etmeksizin yeğen denir."},
                {"Bir kişinin çocuğu olan erkek kişiye ne denir?", "Oğul", "Kız", "Abi", "Kardeş", "Oğul", "Erkek çocuklara oğul denir."},
                {"Kardeşlerden yaşça büyük olan erkek kardeşe ne denir?", "Abi", "Abla", "Kuzen", "Enişte", "Abi", "Büyük erkek kardeşe abi (ağabey) denir."},
                {"Kardeşlerden yaşça büyük olan kız kardeşe ne denir?", "Abla", "Abi", "Yenge", "Hala", "Abla", "Büyük kız kardeşe abla denir."},
                {"Bir erkek evlendiğinde, eşinin annesine ne der?", "Kaynana", "Görümce", "Baldız", "Elti", "Kaynana", "Eşlerin annelerine kaynana (kayınvalide) denir."},
                {"Bir kadın evlendiğinde, eşinin kız kardeşine ne der?", "Görümce", "Elti", "Baldız", "Yenge", "Görümce", "Kocanın kız kardeşi görümcedir."},
                {"Amcanızın veya teyzenizin çocuğuna ne denir?", "Kuzen", "Yeğen", "Kardeş", "Enişte", "Kuzen", "Akraba çocuklarına kuzen denir."},
                {"Yeni evli bir kadına ne denir?", "Gelin", "Damat", "Kayınço", "Bacanak", "Gelin", "Evlenen kadına gelin, erkeğe damat denir."},
                {"Abinizin veya amcanızın karısına ne dersiniz?", "Yenge", "Hala", "Teyze", "Elti", "Yenge", "Erkek akrabaların eşlerine yenge denir."},
                {"Halanızın veya teyzenizin kocasına ne dersiniz?", "Enişte", "Amca", "Dayı", "Kayınço", "Enişte", "Kız akrabaların eşlerine enişte denir."},
                {"Çocuğunuzun çocuğuna ne denir?", "Torun", "Yeğen", "Kuzen", "Evlat", "Torun", "Çocukların çocukları torun olur."},
                {"Hiç kardeşi olmayan çocuğa ne denir?", "Tek çocuk", "İlk çocuk", "Yetim", "Üvey", "Tek çocuk", "Kardeşi olmayanlar için kullanılır."},
                {"Bir erkek evlendiğinde, eşinin erkek kardeşine ne der?", "Kayınço", "Baldız", "Görümce", "Elti", "Kayınço", "Karının erkek kardeşi kayınçodur."},
                {"Kiminle evli olduğunuzu belirten genel terim nedir?", "Eş", "Arkadaş", "Akraba", "Komşu", "Eş", "Karı veya koca için ortak terim eştir."},
                {"Babanızın kız kardeşine ne denir?", "Hala", "Teyze", "Yenge", "Baldız", "Hala", "Babanın kız kardeşi haladır."},
                {"Annenizin babasına ne denir?", "Dede", "Nene", "Amca", "Dayı", "Dede", "Annenin babasına da dede veya büyükbaba denir."}
        };
    }public static String[][] TurkishFamilyMedium() {
        return new String[][] {
                {"Aile Kültürü ve Gelenekler", "Türk aile değerlerini ve atasözlerini keşfedin"},
                {"Türk kültüründe bayramlarda büyüklere saygı için ne yapılır?", "El öpülür", "El sıkışılır", "Sarılınır", "Baş selamı verilir", "El öpülür", "Büyüklerin eli öpülüp alna koyulur, bu saygı göstergesidir."},
                {"'Ağaca çıkan keçinin dala bakan oğlağı olur' atasözü ne anlama gelir?", "Çocuklar anne ve babasını örnek alır", "Keçiler ağaca çıkamaz", "Aileler çok kalabalıktır", "Çocuklar yaramazdır", "Çocuklar anne ve babasını örnek alır", "Çocukların ebeveynlerinin davranışlarını taklit ettiğini anlatır."},
                {"Yeni doğan bir bebeği görmeye gidildiğinde ne takılır?", "Çeyrek altın", "Gümüş kaşık", "Nazar boncuğu", "Saat", "Çeyrek altın", "Geleneksel olarak bebeklere ve yeni evlilere altın takılır."},
                {"İki erkek kardeşin eşleri birbirine ne der?", "Elti", "Görümce", "Baldız", "Kaynana", "Elti", "Erkek kardeşlerin eşleri arasındaki ilişki eltiliktir."},
                {"İki kız kardeşin kocaları birbirine ne der?", "Bacanak", "Kayınço", "Enişte", "Damat", "Bacanak", "Kız kardeşlerin kocaları birbirinin bacanağıdır."},
                {"'Ana gibi yar, Bağdat gibi diyar olmaz' sözü neyi vurgular?", "Annenin eşsizliğini", "Şehirlerin güzelliğini", "Yemeklerin tadını", "Yolculuğun zorluğunu", "Annenin eşsizliğini", "Anne sevgisinin ve değerinin dünyada tek olduğunu ifade eder."},
                {"Düğünden önce kız evinde yapılan eğlenceye ne denir?", "Kına gecesi", "Nişan", "Söz", "Hamam", "Kına gecesi", "Gelin ve davetlilerin ellerine kına yakıldığı geleneksel gecedir."},
                {"'Görücü usulü' ne demektir?", "Tanıdıklar aracılığıyla tanışıp evlenmek", "Kaçarak evlenmek", "Yalnız yaşamak", "Yurt dışında evlenmek", "Tanıdıklar aracılığıyla tanışıp evlenmek", "Geleneksel bir tanışma ve evlenme yöntemidir."},
                {"Türk ailesinde pazar günleri genellikle ne yapılır?", "Ailece kahvaltı yapılır", "İşe gidilir", "Okula gidilir", "Yalnız kalınır", "Ailece kahvaltı yapılır", "Pazar kahvaltısı Türk aileleri için birleştirici bir ritüeldir."},
                {"'Evlilik cüzdanı' nedir?", "Resmi nikah belgesi", "Banka cüzdanı", "Kimlik kartı", "Pasaport", "Resmi nikah belgesi", "Eşlerin resmi olarak evli olduğunu kanıtlayan defter."},
                {"Akrabalar arasında yapılan yardımlaşmaya ne denir?", "İmece", "Ticaret", "Yarışma", "Kavga", "İmece", "Özellikle köylerde işlerin el birliğiyle yapılmasıdır."},
                {"Bebekleri nazardan korumak için ne kullanılır?", "Nazar boncuğu", "Şapka", "Eldiven", "Emzik", "Nazar boncuğu", "Kötü enerjiden koruduğuna inanılan mavi göz simgesi."},
                {"'Söz kesmek' ne anlama gelir?", "Evliliğe giden yolda ilk resmi adım", "Konuşmayı bitirmek", "Kavga etmek", "Yalan söylemek", "Evliliğe giden yolda ilk resmi adım", "Aileler arasında yüzük takılarak verilen ilk sözdür."},
                {"Gelin eve girerken bereket olsun diye ne kırar?", "Testi", "Tabak", "Bardak", "Saksı", "Testi", "İçinden para, şeker veya buğday dökülen testi kırma geleneği vardır."},
                {"'Çeyiz' nedir?", "Gelin adayının hazırladığı ev eşyaları", "Düğün pastası", "Gelinlik", "Davetiye", "Gelin adayının hazırladığı ev eşyaları", "Evlenmeden önce hazırlanan birikim."},
                {"'Ata' kelimesi ne anlama gelir?", "Soy, ecdad, baba", "Çocuk", "Torun", "Kuzen", "Soy, ecdad, baba", "Geçmiş kuşakları ve aile büyüklerini temsil eder."},
                {"Bayramlarda çocuklara ne verilir?", "Harçlık", "Ödev", "Ceza", "Meyve", "Harçlık", "Büyüklerin çocuklara verdiği bayram parasıdır."},
                {"'Abla' kelimesi sadece kardeşe mi söylenir?", "Hayır, saygı için büyüklere de söylenir", "Evet, sadece kardeşe", "Sadece yabancılara", "Sadece annelere", "Hayır, saygı için büyüklere de söylenir", "Türkiye'de yaşça büyük kadınlara saygı ifadesidir."},
                {"'Ocağı tütmek' deyimi neyi ifade eder?", "Ailenin varlığını sürdürmesini", "Yemek pişmesini", "Yangın çıkmasını", "Sigara içilmesini", "Ailenin varlığını sürdürmesini", "Bir ailenin devam etmesi ve yok olmaması anlamındadır."},
                {"Damat adayına kız isteme merasiminde ne ikram edilir?", "Tuzlu kahve", "Şekerli çay", "Ayran", "Süt", "Tuzlu kahve", "Damat adayının sabrını ölçmek için yapılan bir şakadır."}
        };
    }public static String[][] TurkishFamilyHard() {
        return new String[][] {
                {"Soyağacı ve Hukuk", "Derin aile bağları, miras hukuku ve soybilim terimleri"},
                {"Bir ailenin geçmişten günümüze üyelerini gösteren çizelgeye ne denir?", "Soyağacı", "Harita", "İş planı", "Takvim", "Soyağacı", "Şecere olarak da bilinen aile ağacıdır."},
                {"'Veraset' ne anlama gelir?", "Mirasçılık", "Evlilik", "Boşanma", "Doğum", "Mirasçılık", "Ölen bir kişinin mal varlığının hak sahiplerine geçmesi."},
                {"Türk Medeni Kanunu'na göre resmi nikah nerede kıyılır?", "Belediye evlendirme dairesinde", "Camide", "Okulda", "Kütüphanede", "Belediye evlendirme dairesinde", "Türkiye'de sadece resmi nikah yasal olarak geçerlidir."},
                {"'Vesayet' nedir?", "Kendi işini göremeyenlere atanan yasal koruma", "Miras", "Evlat edinme", "Seyahat", "Kendi işini göremeyenlere atanan yasal koruma", "Vasi tayin edilmesi durumudur."},
                {"'Naş' kelimesi neyi ifade eder?", "Cenaze, ölü beden", "Yeni doğan bebek", "Düğün alayı", "Akraba ziyareti", "Cenaze, ölü beden", "Cenaze törenlerinde kullanılan saygılı bir terimdir."},
                {"'Hısım' ne demektir?", "Akraba", "Düşman", "Yabancı", "Komşu", "Akraba", "Özellikle evlilik yoluyla oluşan akrabalıklara 'hısımlık' denir."},
                {"'Velayet' hakkı kime aittir?", "Anne ve babaya", "Hocaya", "Komşuya", "Muhtara", "Anne ve babaya", "Reşit olmayan çocukların korunması ve temsili hakkıdır."},
                {"'Nüfus Kayıt Örneği' neyi gösterir?", "Aile bireylerinin resmi listesini", "Hava durumunu", "Notları", "Alışveriş listesini", "Aile bireylerinin resmi listesini", "E-devlet üzerinden de alınabilen resmi belgedir."},
                {"Kendi öz çocuğu olmadığı halde yasal olarak çocuk alan kişiye ne denir?", "Evlat edinen", "Vasi", "Kayınpeder", "Üvey baba", "Evlat edinen", "Evlatlık edinme hukuki bir süreçtir."},
                {"'İntikal' miras hukukunda neyi ifade eder?", "Mirasın hak sahiplerine geçişi", "Boşanma", "İsim değişikliği", "Adres taşıma", "Mirasın hak sahiplerine geçişi", "Tapu ve mal varlığı devir işlemleridir."},
                {"'Tereke' nedir?", "Ölen kişiden kalan mal ve borçların tamamı", "Yemek masası", "Bahçe", "Kıyafet", "Ölen kişiden kalan mal ve borçların tamamı", "Mirasın hukuki toplamıdır."},
                {"'Süt kardeş' ne demektir?", "Aynı kadından süt emmiş biyolojik olmayan kardeşler", "Süt satan kardeş", "Aynı okulda okuyanlar", "İkizler", "Aynı kadından süt emmiş biyolojik olmayan kardeşler", "İslam hukukunda evlenmeleri yasak olan bir bağdır."},
                {"'Soyadı Kanunu' Türkiye'de ne zaman kabul edilmiştir?", "1934", "1923", "1950", "1900", "1934", "Atatürk devrimlerinden biri olup ailelere sabit bir ad verilmiştir."},
                {"'Müris' kime denir?", "Miras bırakan kişi", "Miras alan kişi", "Avukat", "Hakim", "Miras bırakan kişi", "Hukuk dilinde ölen ve miras bırakan kişidir."},
                {"'Saklı pay' nedir?", "Mirasçıların kanunen korunan payı", "Gizli hazine", "Yemek payı", "Oyun payı", "Mirasçıların kanunen korunan payı", "Vasiyetname ile bile başkasına verilemeyen zorunlu miras payıdır."},
                {"'Kayyım' nedir?", "Belirli bir işi yönetmek için atanan görevli", "Aile büyüğü", "Küçük kardeş", "Nikah memuru", "Belirli bir işi yönetmek için atanan görevli", "Geçici yasal temsilcidir."},
                {"'Nafaka' ne zaman ödenir?", "Boşanma sonrası maddi destek için", "Bayramlarda", "Doğumlarda", "Mezuniyetlerde", "Boşanma sonrası maddi destek için", "Mahkeme kararıyla bağlanan aylık ödeme."},
                {"'Ecdad' ne demektir?", "Geçmişteki büyükler, atalar", "Gelecek kuşaklar", "Arkadaşlar", "Kardeşler", "Geçmişteki büyükler, atalar", "Bir kişinin köklerini ve eski aile üyelerini kapsar."},
                {"'Muhafazakar aile' neyi temsil eder?", "Gelenek ve değerlerine bağlı aile", "Tek başına yaşayan", "Sürekli gezen", "Kuralları olmayan", "Gelenek ve değerlerine bağlı aile", "Geleneksel yaşam tarzını benimseyen ailedir."},
                {"'Vasiyetname' ne zaman geçerli olur?", "Kişinin ölümünden sonra", "Evlenirken", "Çocuk doğunca", "İş kurunca", "Kişinin ölümünden sonra", "Kişinin ölmeden önce yazılı olarak bıraktığı son istekleridir."}
        };
    }
    private void seedForFamilyTurkish(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = TurkishFamilyEasy();
        var md = TurkishFamilyMedium();
        var h = TurkishFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] DutchFamilyEasy() {
        return new String[][] {
                {"Mijn Familie", "Leer de basiswoorden voor familieleden in het Nederlands"},
                {"Hoe noem je de broer van je vader?", "Oom", "Nefje", "Neef", "Zwager", "Oom", "De broer van je vader of moeder is je oom."},
                {"De dochter van je zus is je...", "Nichtje", "Kleindochter", "Zus", "Stiefdochter", "Nichtje", "Het kind van je broer of zus is een neefje (jongen) of nichtje (meisje)."},
                {"Hoe noem je de moeder van je vrouw?", "Schoonmoeder", "Stiefmoeder", "Tante", "Oma", "Schoonmoeder", "De ouders van je partner zijn je schoonouders."},
                {"Wie is de zoon van je oom?", "Je neef", "Je broer", "Je opa", "Je zwager", "Je neef", "Kinderen van ooms en tantes noem je neef of nicht."},
                {"Hoe noem je een mannelijk kind?", "Zoon", "Dochter", "Broer", "Kleinzoon", "Zoon", "Een mannelijk kind is een zoon, een vrouwelijk kind is een dochter."},
                {"Als je vader hertrouwt, is zijn nieuwe vrouw je...", "Stiefmoeder", "Schoonmoeder", "Tante", "Oma", "Stiefmoeder", "Een niet-biologische moeder door huwelijk is een stiefmoeder."},
                {"Hoe noem je de vader van je vader?", "Opa", "Overgrootvader", "Oom", "Schoonvader", "Opa", "Grootouders worden meestal opa en oma genoemd."},
                {"Wat is de vrouwelijke vorm van 'Schoonzoon'?", "Schoondochter", "Nichten", "Zus", "Tante", "Schoondochter", "De partner van je kind is je schoonzoon of schoondochter."},
                {"Wie zijn je 'Broers en zussen'?", "Kinderen van dezelfde ouders", "Je neven", "Je opa's", "Je ooms", "Kinderen van dezelfde ouders", "In het Nederlands gebruiken we vaak 'broers en zussen' in plaats van één woord zoals siblings."},
                {"Hoe noem je de zus van je moeder?", "Tante", "Oma", "Nicht", "Nichtje", "Tante", "De zus van je vader of moeder is je tante."},
                {"Wat is een 'Enig kind'?", "Een kind zonder broers of zussen", "Het oudste kind", "Het jongste kind", "Een tweeling", "Een kind zonder broers of zussen", "Iemand die alleen is opgegroeid zonder broers of zussen."},
                {"Wie is je 'Echtgenoot'?", "Je man of vrouw", "Je broer", "Je neef", "Je buurman", "Je man of vrouw", "Een formele term voor de persoon met wie je getrouwd bent."},
                {"Hoe noem je het kind van je kind?", "Kleinkind", "Achterkleinkind", "Neefje", "Oom", "Kleinkind", "De kinderen van je kinderen zijn je kleinkinderen."},
                {"Wie is je 'Moeder'?", "Je moeder", "Je oma", "Je tante", "Je zus", "Je moeder", "Andere woorden zijn mama, mam of mams."},
                {"Hoe noem je de broer van je vrouw?", "Zwager", "Schoonvader", "Oom", "Broer", "Zwager", "Een zwager is de broer van je partner of de man van je zus."},
                {"Wie is de 'Zus'?", "De dochter van je ouders", "De dochter van je tante", "Je moeder", "Je oma", "De dochter van je ouders", "Een vrouwelijk familielid van dezelfde generatie."},
                {"Wat zijn 'Verwanten'?", "Al je familieleden", "Alleen je ouders", "Je vrienden", "Je buren", "Al je familieleden", "Mensen die door bloed of huwelijk aan je verbonden zijn."},
                {"Wie is je 'Oma'?", "De moeder van je ouders", "De zus van je moeder", "De dochter van je dochter", "Je schoonmoeder", "De moeder van je ouders", "Ook wel grootmoeder genoemd."},
                {"Hoe noem je de man van je zus?", "Zwager", "Schoonvader", "Oom", "Neef", "Zwager", "De echtgenoot van je broer of zus is je zwager."},
                {"Wie is je 'Vader'?", "Je vader", "Je opa", "Je oom", "Je zoon", "Je vader", "Ook wel papa, pap of pa genoemd."}
        };
    }public static String[][] DutchFamilyMedium() {
        return new String[][] {
                {"Cultuur en Tradities", "Begrijp de Nederlandse familiegebruiken en gezegden"},
                {"Wat eten Nederlanders traditioneel als er een baby is geboren?", "Beschuit met muisjes", "Pannenkoeken", "Stamppot", "Appeltaart", "Beschuit met muisjes", "Blauwe muisjes voor een jongen, roze voor een meisje."},
                {"Wat betekent 'Zo vader, zo zoon'?", "Kinderen lijken op hun ouders", "Kinderen zijn heel anders", "De vader is lang", "De familie is rijk", "Kinderen lijken op hun ouders", "Het betekent dat kinderen vaak hetzelfde karakter of uiterlijk hebben als hun vader."},
                {"Wat is een 'Oma-dag'?", "Een dag waarop oma op de kleinkinderen past", "Een verjaardag", "Een feestdag", "Een dag om te winkelen", "Een dag waarop oma op de kleinkinderen past", "Veel grootouders in Nederland hebben een vaste oppasdag."},
                {"Wie is de 'Stambouwer'?", "De persoon die de familiegeschiedenis uitzoekt", "De vader", "De jongste zoon", "De timman", "De persoon die de familiegeschiedenis uitzoekt", "Iemand die onderzoek doet naar de stamboom."},
                {"Wat betekent het woord 'Gezellig' in een familiecontext?", "Druk en chaotisch", "Warm, knus en plezierig samen", "Heel saai", "Alleen zijn", "Warm, knus en plezierig samen", "Het hart van de Nederlandse cultuur en familiebezoeken."},
                {"Wie is het 'Zwarte schaap' van de familie?", "Het lievelingskind", "Iemand die buiten de groep valt of problemen veroorzaakt", "Het oudste lid", "Iemand met zwart haar", "Iemand die buiten de groep valt of problemen veroorzaakt", "Iemand die zich anders gedraagt dan de rest van de familie."},
                {"Wat vieren Nederlanders op 5 december met de familie?", "Sinterklaas", "Kerstmis", "Pasen", "Koningsdag", "Sinterklaas", "Een belangrijk familiefeest met cadeautjes en gedichten."},
                {"Wat is een 'Koffietijd' bij familie?", "Tijd om te slapen", "Samen koffie drinken en praten (vaak om 10:00 of 15:00)", "Hard werken", "Sporten", "Samen koffie drinken en praten (vaak om 10:00 of 15:00)", "Een typisch Nederlands sociaal moment."},
                {"Wat betekent 'De vuile was buiten hangen'?", "De was doen", "Privéproblemen van de familie aan anderen vertellen", "Nieuwe kleren kopen", "Verhuizen", "Privéproblemen van de familie aan anderen vertellen", "Het delen van familiegeheimen met vreemden."},
                {"Wat doen veel gezinnen op zaterdagmorgen?", "Langs het sportveld staan bij de kinderen", "Uitslapen tot 12 uur", "Werken", "Naar de kerk", "Langs het sportveld staan bij de kinderen", "Nederland heeft een sterke amateur-sportcultuur."},
                {"Wat is 'Polderen' in een gezin?", "Ruzie maken", "Overleggen tot er een compromis is", "Wandelen in de polder", "Schoonmaken", "Overleggen tot er een compromis is", "Nederlanders praten veel om samen beslissingen te nemen."},
                {"Wat betekent 'Je eigen vlees en bloed'?", "Je eten", "Je directe biologische familie", "Je vrienden", "Je buren", "Je directe biologische familie", "Het benadrukt de sterke band tussen ouders en kinderen."},
                {"Wat vieren veel Nederlanders als ze 50 jaar worden?", "Abraham of Sarah zien", "Een klein feestje", "Niets", "Het pensioen", "Abraham of Sarah zien", "Er wordt vaak een grote pop in de tuin gezet."},
                {"Wat is een 'Samengesteld gezin'?", "Een gezin met stiefouders en stiefkinderen", "Een gezin met 10 kinderen", "Een gezin dat ver weg woont", "Alleen een vader en zoon", "Een gezin met stiefouders en stiefkinderen", "Ook wel een 'bonusgezin' genoemd."},
                {"Wat is een 'Kraamvisite'?", "Op bezoek gaan bij een nieuwe baby", "Naar het ziekenhuis gaan", "Een vakantie", "Een huwelijksfeest", "Op bezoek gaan bij een nieuwe baby", "Vrienden en familie komen de baby bewonderen."},
                {"Wat betekent 'Het nest verlaten'?", "Gaan vliegen", "Uit huis gaan wonen (zelfstandig worden)", "Gaan slapen", "Een vogelhuisje bouwen", "Uit huis gaan wonen (zelfstandig worden)", "Wanneer kinderen op kamers gaan of gaan samenwonen."},
                {"Wat is een 'Gouden bruiloft'?", "10 jaar getrouwd", "25 jaar getrouwd", "50 jaar getrouwd", "Een verloving", "50 jaar getrouwd", "Een grote mijlpaal voor een echtpaar."},
                {"Wat is een 'Kliekjesdag'?", "Een dag om kleren te kopen", "Een dag waarop restjes eten van de vorige dag worden gegeten", "Een feestdag", "Een dag om te tuinieren", "Een dag waarop restjes eten van de vorige dag worden gegeten", "Vaak een informele gezinsavond."},
                {"Wat betekent 'De appel valt niet ver van de boom'?", "Appels zijn lekker", "Kinderen lijken op hun ouders", "De boom is klein", "De oogst is goed", "Kinderen lijken op hun ouders", "Hetzelfde als 'Zo vader, zo zoon'."},
                {"Hoe noemen Nederlanders vaak hun ouders informeel?", "Pa en moe", "Meneer en mevrouw", "Opa en oma", "Vriend en vriendin", "Pa en moe", "Of simpelweg papa en mama."}
        };
    }public static String[][] DutchFamilyHard() {
        return new String[][] {
                {"Erfenis en Genealogie", "Verken complexe familiebanden en juridische termen"},
                {"Wat is een 'Stamboom'?", "Een boom in het bos", "Een grafische weergave van familiebanden", "Een geschiedenisboek", "Een landkaart", "Een grafische weergave van familiebanden", "Het laat zien wie de voorouders zijn."},
                {"Wat betekent 'Bloedverwant'?", "Een goede vriend", "Iemand met wie je een biologische band hebt", "Een aangetrouwd familielid", "Een collega", "Iemand met wie je een biologische band hebt", "Mensen met dezelfde biologische afstamming."},
                {"Wie is de 'Erfgenaam'?", "De persoon die de schulden betaalt", "De persoon die bezittingen krijgt na een overlijden", "De oudste zoon", "Een buurman", "De persoon die bezittingen krijgt na een overlijden", "De wettelijke opvolger van iemands bezit."},
                {"Wat is 'Nalatenschap'?", "Een cadeau", "Alles wat iemand na zijn dood achterlaat (bezit en schuld)", "Een verre reis", "Een diploma", "Alles wat iemand na zijn dood achterlaat (bezit en schuld)", "Het geheel van goederen en schulden van een overledene."},
                {"Wat betekent 'Ouderlijk gezag'?", "De macht van de overheid", "De wettelijke plichten en rechten van ouders voor hun kinderen", "Een feestdag", "Een type belasting", "De wettelijke plichten en rechten van ouders voor hun kinderen", "Het recht om beslissingen te nemen voor een minderjarig kind."},
                {"Wie zijn de 'Voorouders'?", "De kleinkinderen", "De generaties vóór de ouders (grootouders, etc.)", "De broers", "De buren", "De generaties vóór de ouders (grootouders, etc.)", "De mensen van wie de familie afstamt."},
                {"Wat is een 'Testament'?", "Een belofte", "Een officieel document over de verdeling van bezit na de dood", "Een geboorteakte", "Een uitnodiging", "Een officieel document over de verdeling van bezit na de dood", "Vastgelegd bij een notaris."},
                {"Wat betekent 'Aangetrouwd'?", "Familie door bloed", "Familie door het huwelijk van een bloedverwant", "Beste vrienden", "Geen familie", "Familie door het huwelijk van een bloedverwant", "Zoals een schoonzus of zwager."},
                {"Wie is het 'Hoofd van de familie'?", "De jongste zoon", "De persoon met de meeste autoriteit (vaak traditioneel)", "Een advocaat", "Een kind", "De persoon met de meeste autoriteit (vaak traditioneel)", "Tegenwoordig minder gebruikelijk in Nederland door gelijkwaardigheid."},
                {"Wat is een 'Uittreksel uit het geboorteregister'?", "Een dagboek", "Een officieel bewijs van iemands geboorte en ouders", "Een trouwfoto", "Een krantenartikel", "Een officieel bewijs van iemands geboorte en ouders", "Op te vragen bij de gemeente."},
                {"Wat betekent 'Eerste graads' familie?", "Ouders en kinderen", "Neven en nichten", "Ooms en tantes", "Vrienden", "Ouders en kinderen", "De meest directe bloedverwanten."},
                {"Wat is 'Geregistreerd partnerschap'?", "Samenwonen zonder regels", "Een wettelijke samenlevingsvorm vergelijkbaar met het huwelijk", "Een vriendschap", "Een zakelijk contract", "Een wettelijke samenlevingsvorm vergelijkbaar met het huwelijk", "Een populaire alternatieve vorm voor trouwen in Nederland."},
                {"Wie is een 'Voogd'?", "Een leraar", "Iemand die verantwoordelijk is voor een kind als de ouders dat niet kunnen", "Een verre neef", "Een buurman", "Iemand die verantwoordelijk is voor een kind als de ouders dat niet kunnen", "Wettelijk aangewezen beschermer."},
                {"Wat betekent 'Affiniteit' in juridische zin?", "Sympathie", "De band tussen een persoon en de bloedverwanten van zijn partner", "Hobby's", "Bloedverwantschap", "De band tussen een persoon en de bloedverwanten van zijn partner", "Verwant door huwelijk."},
                {"Wat is 'Adoptie'?", "Een tijdelijk verblijf", "Het wettelijk aannemen van een kind van andere ouders", "Vakantie vieren", "Op bezoek gaan", "Het wettelijk aannemen van een kind van andere ouders", "Hierdoor wordt het kind wettelijk volledig eigen kind."},
                {"Wat betekent 'Legitieme portie'?", "Een eerlijk deel", "Het wettelijk minimumdeel van de erfenis voor kinderen", "Een wet van de staat", "Een handtekening", "Het wettelijk minimumdeel van de erfenis voor kinderen", "Kinderen kunnen niet zomaar volledig onterfd worden."},
                {"Wat is 'Huwelijkse voorwaarden'?", "De regels voor het feest", "Afspraken over bezit vóór het huwelijk", "Ruzie maken", "Kleren delen", "Afspraken over bezit vóór het huwelijk", "Vastgelegd bij de notaris om bezit gescheiden te houden."},
                {"Wie zijn de 'Nazaat'?", "De voorouders", "De nakomelingen (kinderen, kleinkinderen)", "De buren", "De vrienden", "De nakomelingen (kinderen, kleinkinderen)", "Zij die na de huidige generatie komen."},
                {"Wat is een 'Kwartierstaat'?", "Een type klok", "Een overzicht van alle directe voorouders van één persoon", "Een wegenkaart", "Een kwartaalverslag", "Een overzicht van alle directe voorouders van één persoon", "Een term uit de genealogie."},
                {"Wat betekent 'Erfopvolging'?", "Een spelletje spelen", "De volgorde waarin familieleden recht hebben op een erfenis", "Verhuizen", "Huwelijk", "De volgorde waarin familieleden recht hebben op een erfenis", "Wettelijk bepaald wie als eerste recht heeft op de erfenis."}
        };
    }
    private void seedForFamilyDutch(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = DutchFamilyEasy();
        var md = DutchFamilyMedium();
        var h = DutchFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ThaiFamilyEasy() {
        return new String[][] {
                {"ครอบครัวของฉัน", "เรียนรู้คำศัพท์พื้นฐานเกี่ยวกับสมาชิกในครอบครัว"},
                {"พี่ชายของพ่อเรียกว่าอะไร?", "ลุง", "ป้า", "น้า", "อา", "ลุง", "พี่ชายของพ่อหรือแม่เรียกว่า ลุง"},
                {"น้องสาวของแม่เรียกว่าอะไร?", "ป้า", "น้า", "อา", "ย่า", "น้า", "น้องของแม่เรียกว่า น้า ไม่ว่าจะเป็นชายหรือหญิง"},
                {"พ่อของพ่อเรียกว่าอะไร?", "ปู่", "ย่า", "ตา", "ยาย", "ปู่", "พ่อของพ่อคือ ปู่ ส่วนพ่อของแม่คือ ตา"},
                {"ลูกของพี่สาวเรียกว่าอะไร?", "หลาน", "ลูก", "พี่", "น้อง", "หลาน", "ลูกของพี่หรือน้องเรียกว่า หลาน"},
                {"ภรรยาของลูกชายเรียกว่าอะไร?", "ลูกสะใภ้", "ลูกเขย", "หลาน", "พี่สะใภ้", "ลูกสะใภ้", "สามีของลูกสาวเรียก ลูกเขย ภรรยาของลูกชายเรียก ลูกสะใภ้"},
                {"แม่ของแม่เรียกว่าอะไร?", "ย่า", "ยาย", "ป้า", "น้า", "ยาย", "แม่ของแม่คือ ยาย ส่วนแม่ของพ่อคือ ย่า"},
                {"น้องชายของพ่อเรียกว่าอะไร?", "น้า", "อา", "ลุง", "ป้า", "อา", "น้องของพ่อเรียกว่า อา ไม่ว่าจะเป็นชายหรือหญิง"},
                {"ลูกชายของน้าเรียกว่าอะไร?", "พี่น้อง", "ลูกพี่ลูกน้อง", "หลาน", "เพื่อน", "ลูกพี่ลูกน้อง", "ลูกของลุง ป้า น้า อา คือ ลูกพี่ลูกน้อง"},
                {"คำเรียกพี่สาวของพ่อหรือแม่คือ?", "ป้า", "น้า", "อา", "แม่", "ป้า", "พี่สาวของพ่อหรือแม่เรียกว่า ป้า"},
                {"สามีของลูกสาวเรียกว่าอะไร?", "ลูกเขย", "ลูกสะใภ้", "หลาน", "น้องเขย", "ลูกเขย", "สามีของลูกสาวเรียกว่า ลูกเขย"},
                {"ถ้าคุณเป็นผู้ชาย น้องสาวของภรรยาคือ?", "น้องเมีย", "น้องสะใภ้", "พี่เมีย", "น้องสาว", "น้องเมีย", "ญาติทางฝั่งภรรยาหรือสามีจะมีคำเรียกเฉพาะ"},
                {"พี่ชายของภรรยาเรียกว่าอะไร?", "พี่เขย", "พี่เมีย", "น้องเมีย", "ลุง", "พี่เมีย", "พี่ชายของภรรยาเรียก พี่เมีย"},
                {"ลูกที่ไม่มีพี่น้องเลยเรียกว่า?", "ลูกหลง", "ลูกคนเดียว", "ลูกคนโต", "ลูกคนเล็ก", "ลูกคนเดียว", "ครอบครัวที่มีลูกเพียงคนเดียว"},
                {"พ่อของภรรยาเรียกว่าอะไร?", "พ่อตา", "พ่อสามี", "ปู่", "ตา", "พ่อตา", "พ่อของภรรยาเรียก พ่อตา แม่ของภรรยาเรียก แม่ยาย"},
                {"แม่ของสามีเรียกว่าอะไร?", "แม่ยาย", "แม่สามี", "ย่า", "ยาย", "แม่สามี", "คนไทยมักเรียกแม่ของสามีว่า แม่สามี หรือ คุณแม่"},
                {"พี่ชายแท้ๆ เรียกว่าอะไร?", "น้องชาย", "พี่ชาย", "ลุง", "น้า", "พี่ชาย", "คนไทยให้ความสำคัญกับลำดับอาวุโส พี่จึงต้องมีคำเรียกต่างจากน้อง"},
                {"เด็กที่เกิดจากพ่อแม่เดียวกันคือ?", "เพื่อน", "พี่น้อง", "ญาติ", "คนรู้จัก", "พี่น้อง", "ใช้เรียกคนที่เกิดจากพ่อแม่เดียวกัน"},
                {"ลูกคนสุดท้ายของครอบครัวเรียกว่า?", "ลูกคนโต", "ลูกคนกลาง", "ลูกคนเล็ก/ลูกหลง", "ลูกบุญธรรม", "ลูกคนเล็ก/ลูกหลง", "ลูกคนสุดท้องของบ้าน"},
                {"คำสุภาพที่ใช้เรียกพ่อคือ?", "ป๊า", "คุณพ่อ", "บิดา", "นาย", "คุณพ่อ", "คุณพ่อ เป็นคำเรียกที่สุภาพและเป็นทางการ"},
                {"ปู่ ย่า ตา ยาย รวมเรียกว่า?", "คนแก่", "ตายาย", "บรรพบุรุษ", "ญาติผู้ใหญ่", "ญาติผู้ใหญ่", "ใช้เรียกญาติที่มีอายุมากกว่าพ่อแม่"}
        };
    }public static String[][] ThaiFamilyMedium() {
        return new String[][] {
                {"วัฒนธรรมและวิถีไทย", "เข้าใจขนบธรรมเนียมและการให้ความสำคัญกับครอบครัวของคนไทย"},
                {"สำนวน 'ลูกไม้หล่นไม่ไกลต้น' หมายถึง?", "ลูกมีนิสัยเหมือนพ่อแม่", "ลูกไม่รักพ่อแม่", "ลูกไปทำงานไกลบ้าน", "ลูกดื้อ", "ลูกมีนิสัยเหมือนพ่อแม่", "เปรียบเปรยว่าเด็กมักจะรับนิสัยหรือความสามารถมาจากพ่อแม่"},
                {"คนไทยแสดงความเคารพญาติผู้ใหญ่ด้วยวิธีใด?", "จับมือ", "กอด", "ไหว้", "จูบแก้ม", "ไหว้", "การไหว้เป็นการแสดงความเคารพที่เป็นเอกลักษณ์ของไทย"},
                {"'ความกตัญญู' ในครอบครัวไทยหมายถึง?", "การตอบแทนพระคุณพ่อแม่", "การขอเงินพ่อแม่", "การดื้อรั้น", "การหนีออกจากบ้าน", "การตอบแทนพระคุณพ่อแม่", "ความกตัญญูถือเป็นเครื่องหมายของคนดีในสังคมไทย"},
                {"วันใดที่เป็นวันครอบครัวของไทย?", "13 เมษายน (สงกรานต์)", "1 มกราคม", "25 ธันวาคม", "14 กุมภาพันธ์", "13 เมษายน (สงกรานต์)", "ช่วงสงกรานต์เป็นเวลาที่คนไทยกลับบ้านไปหาครอบครัว"},
                {"ประเพณี 'รดน้ำดำหัว' ทำเพื่ออะไร?", "ขอขมาและขอพรจากผู้ใหญ่", "เล่นสนุก", "ล้างบ้าน", "อาบน้ำให้เด็ก", "ขอขมาและขอพรจากผู้ใหญ่", "นิยมทำในช่วงเทศกาลสงกรานต์"},
                {"คำว่า 'บ้านแตกสาแหรกขาด' หมายถึง?", "บ้านพัง", "ครอบครัวกระจัดกระจาย/ทะเลาะกัน", "บ้านไม่มีหลังคา", "บ้านรวยมาก", "ครอบครัวกระจัดกระจาย/ทะเลาะกัน", "เป็นสำนวนหมายถึงครอบครัวที่ล่มสลาย"},
                {"คนไทยมักจะกลับบ้านต่างจังหวัดในช่วงเทศกาลใดมากที่สุด?", "สงกรานต์", "ฮาโลวีน", "วันเด็ก", "วันครู", "สงกรานต์", "ถือเป็นวันรวมญาติครั้งใหญ่ของปี"},
                {"การเรียกคนที่ไม่ใช่ญาติว่า 'พี่' หรือ 'น้า' แสดงถึงอะไร?", "ความสนิทสนมและให้เกียรติ", "ความโกรธ", "การดูถูก", "ความไม่พอใจ", "ความสนิทสนมและให้เกียรติ", "คนไทยนิยมใช้คำญาติเรียกคนทั่วไปเพื่อความสุภาพ"},
                {"'สินสอด' คืออะไร?", "เงินที่ฝ่ายชายมอบให้ครอบครัวฝ่ายหญิง", "ของขวัญวันเกิด", "เงินกู้", "รางวัลเรียนดี", "เงินที่ฝ่ายชายมอบให้ครอบครัวฝ่ายหญิง", "เป็นประเพณีการแต่งงานของไทย"},
                {"คำว่า 'ก้นกุฏิ' มักใช้เรียกใคร?", "คนในครอบครัวที่ไว้วางใจที่สุด", "ลูกคนโต", "คนใช้", "ศัตรู", "คนในครอบครัวที่ไว้วางใจที่สุด", "หมายถึงคนที่ใกล้ชิดและเชื่อใจได้มาก"},
                {"สำนวน 'ดูช้างให้ดูหาง ดูนางให้ดูแม่' หมายถึง?", "อยากรู้ว่าผู้หญิงเป็นคนอย่างไรให้ดูที่แม่", "ให้เลี้ยงช้าง", "ให้ไปเที่ยวสวนสัตว์", "แม่เป็นคนสวย", "อยากรู้ว่าผู้หญิงเป็นคนอย่างไรให้ดูที่แม่", "เป็นสำนวนไทยเกี่ยวกับการเลือกคู่ครอง"},
                {"คนไทยเรียกแทนตัวเองเวลาคุยกับผู้ใหญ่ว่าอย่างไร?", "ชื่อเล่น หรือ หนู/ผม", "เรา", "แก", "ท่าน", "ชื่อเล่น หรือ หนู/ผม", "การใช้ชื่อเล่นแทนตัวเองแสดงถึงความอ่อนน้อม"},
                {"'แม่ศรีเรือน' หมายถึงหญิงที่มีลักษณะอย่างไร?", "เก่งงานบ้านงานเรือน", "ทำงานนอกบ้านเก่ง", "แต่งตัวสวยอย่างเดียว", "ชอบเที่ยว", "เก่งงานบ้านงานเรือน", "เป็นลักษณะหญิงไทยในอุดมคติสมัยก่อน"},
                {"การ 'ตัดหางปล่อยวัด' หมายถึง?", "ทำความสะอาดวัด", "ตัดขาดจากลูกหลานที่ทำตัวไม่ดี", "พาสัตว์ไปปล่อย", "เลี้ยงสุนัขในวัด", "ตัดขาดจากลูกหลานที่ทำตัวไม่ดี", "หมายถึงการไม่รับผิดชอบหรือตัดความสัมพันธ์"},
                {"เมื่อพ่อแม่แก่ตัวลง ลูกไทยมีหน้าที่อย่างไร?", "ดูแลเลี้ยงดู (ปรนนิบัติ)", "ส่งไปบ้านพักคนชราทันที", "ทอดทิ้ง", "ให้พ่อแม่ทำงานเลี้ยงลูก", "ดูแลเลี้ยงดู (ปรนนิบัติ)", "การเลี้ยงดูพ่อแม่ยามแก่เฒ่าเป็นหน้าที่สำคัญของลูกไทย"},
                {"'ทำบุญขึ้นบ้านใหม่' มีวัตถุประสงค์เพื่อ?", "เพื่อความเป็นสิริมงคลของผู้อยู่อาศัย", "เพื่ออวดความรวย", "เพื่อขายบ้าน", "เพื่อไล่ที่", "เพื่อความเป็นสิริมงคลของผู้อยู่อาศัย", "เป็นพิธีกรรมเมื่อย้ายเข้าบ้านใหม่"},
                {"'พึ่งพาอาศัยกัน' เป็นลักษณะของครอบครัวแบบใด?", "ครอบครัวขยายที่ช่วยเหลือกัน", "ครอบครัวที่ตัวใครตัวมัน", "ครอบครัวที่ทะเลาะกัน", "คนแปลกหน้า", "ครอบครัวขยายที่ช่วยเหลือกัน", "สังคมไทยให้ความสำคัญกับการช่วยเหลือคนในครอบครัว"},
                {"ลูกที่ดูแลพ่อแม่เป็นอย่างดีเรียกว่าเป็นคนอย่างไร?", "คนกตัญญู", "คนเห็นแก่ตัว", "คนร่ำรวย", "คนขี้เกียจ", "คนกตัญญู", "เป็นคำยกย่องที่สุดในสังคมไทย"},
                {"'ครอบครัวขยาย' ในไทยมักจะอยู่ร่วมกับใคร?", "ปู่ ย่า ตา ยาย ลุง ป้า", "อยู่คนเดียว", "อยู่กับเพื่อน", "อยู่กับคนเช่าบ้าน", "ปู่ ย่า ตา ยาย ลุง ป้า", "ครอบครัวไทยสมัยก่อนมักอยู่รวมกันเป็นบ้านใหญ่"},
                {"คำว่า 'ขวัญ' ในความเชื่อไทยเกี่ยวกับเด็กหมายถึง?", "สิ่งสิริมงคลที่ปกป้องเด็ก", "ของเล่น", "ขนม", "ชื่อเล่น", "สิ่งสิริมงคลที่ปกป้องเด็ก", "มีการทำขวัญเดือนให้เด็กแรกเกิด"}
        };
    }public static String[][] ThaiFamilyHard() {
        return new String[][] {
                {"วงศ์ตระกูลและกฎหมาย", "เรียนรู้คำศัพท์ระดับสูงและคำราชาศัพท์เกี่ยวกับเครือญาติ"},
                {"คำราชาศัพท์ของ 'พ่อ' คือ?", "พระชนก", "พระชนนี", "พระเชษฐา", "พระอนุชา", "พระชนก", "พระชนก หมายถึง พ่อ ส่วน พระชนนี หมายถึง แม่"},
                {"'สืบสันดาน' ตามกฎหมายหมายถึง?", "ผู้สืบเชื้อสายโดยตรง (ลูก หลาน เหลน)", "เพื่อนบ้าน", "ญาติห่างๆ", "ลูกบุญธรรม", "ผู้สืบเชื้อสายโดยตรง (ลูก หลาน เหลน)", "เป็นคำศัพท์ทางกฎหมายมรดก"},
                {"'พระอัยกา' ในคำราชาศัพท์หมายถึง?", "ปู่ หรือ ตา", "ย่า หรือ ยาย", "พี่ชาย", "น้องชาย", "ปู่ หรือ ตา", "พระอัยกา (ปู่/ตา) พระอัยยิกา (ย่า/ยาย)"},
                {"'สินส่วนตัว' คือทรัพย์สินแบบใด?", "ทรัพย์สินที่ได้มาก่อนสมรส", "ทรัพย์สินที่ได้มาหลังแต่งงาน", "เงินของเพื่อน", "เงินของบริษัท", "ทรัพย์สินที่ได้มาก่อนสมรส", "ใช้ในกฎหมายครอบครัวไทย"},
                {"'พระเชษฐา' ในคำราชาศัพท์หมายถึง?", "พี่ชาย", "น้องชาย", "พี่สาว", "น้องสาว", "พี่ชาย", "พระเชษฐา (พี่ชาย) พระอนุชา (น้องชาย)"},
                {"'สินสมรส' คือทรัพย์สินที่ได้มาเมื่อใด?", "ระหว่างสมรส (หลังแต่งงาน)", "ก่อนแต่งงาน", "หลังหย่า", "ตอนเป็นเด็ก", "ระหว่างสมรส (หลังแต่งงาน)", "ทรัพย์สินที่สามีภรรยาหามาได้ร่วมกัน"},
                {"'พินัยกรรม' คือเอกสารเกี่ยวกับอะไร?", "การยกมรดกให้ผู้อื่นหลังเสียชีวิต", "การซื้อของ", "การสมัครงาน", "การเรียน", "การยกมรดกให้ผู้อื่นหลังเสียชีวิต", "เอกสารแสดงเจตนาเรื่องทรัพย์สิน"},
                {"'ทายาทโดยธรรม' หมายถึงใคร?", "ผู้มีสิทธิรับมรดกตามกฎหมาย", "เพื่อนสนิท", "คนใช้", "ลูกหนี้", "ผู้มีสิทธิรับมรดกตามกฎหมาย", "ญาติที่มีสิทธิตามลำดับที่กฎหมายกำหนด"},
                {"'บุตรบุญธรรม' มีสิทธิเท่าเทียมกับลูกแท้ๆ ในเรื่องใด?", "การรับมรดก (ถ้าจดทะเบียน)", "ไม่มีสิทธิเลย", "สิทธิในการเป็นเจ้าของพ่อแม่", "สิทธิในการเปลี่ยนหน้าตา", "การรับมรดก (ถ้าจดทะเบียน)", "กฎหมายไทยคุ้มครองบุตรบุญธรรมที่จดทะเบียนถูกต้อง"},
                {"'พระมาตุลา' ในคำราชาศัพท์หมายถึง?", "น้าชาย หรือ ลุงข้างแม่", "น้าสาว", "อาชาย", "อาสาว", "น้าชาย หรือ ลุงข้างแม่", "คำราชาศัพท์เรียกญาติฝั่งแม่"},
                {"การ 'จดทะเบียนสมรส' มีความสำคัญอย่างไร?", "ทำให้เป็นสามีภรรยาที่ถูกต้องตามกฎหมาย", "ทำให้รวยขึ้น", "ทำให้สวยขึ้น", "ทำให้ไม่ต้องทำงาน", "ทำให้เป็นสามีภรรยาที่ถูกต้องตามกฎหมาย", "เป็นการคุ้มครองสิทธิของทั้งสองฝ่าย"},
                {"'วงศ์ตระกูล' หมายถึง?", "เชื้อสายที่สืบต่อกันมา", "ชื่อหมู่บ้าน", "ชื่อโรงเรียน", "ชื่อวัด", "เชื้อสายที่สืบต่อกันมา", "การรักษาชื่อเสียงของวงศ์ตระกูลเป็นเรื่องสำคัญ"},
                {"'อนุภรรยา' เป็นคำโบราณหมายถึง?", "เมียน้อย", "เมียหลวง", "ลูกสาว", "พี่สาว", "เมียน้อย", "ปัจจุบันกฎหมายไทยให้จดทะเบียนสมรสได้เพียงใบเดียว"},
                {"'พระขนิษฐา' ในคำราชาศัพท์หมายถึง?", "น้องสาว", "พี่สาว", "น้องชาย", "พี่ชาย", "น้องสาว", "พระเชษฐภคินี (พี่สาว) พระขนิษฐา (น้องสาว)"},
                {"'การสมรสซ้อน' ในกฎหมายไทยผลคือ?", "การสมรสเป็นโมฆะ", "รวยเป็นสองเท่า", "ไม่ผิดกฎหมาย", "ได้รางวัล", "การสมรสเป็นโมฆะ", "กฎหมายไทยห้ามจดทะเบียนสมรสซ้อน"},
                {"'ลำดับสืบราชสันตติวงศ์' เกี่ยวข้องกับอะไร?", "การสืบทอดตำแหน่งพระมหากษัตริย์", "การแข่งกีฬา", "การเลือกตั้ง", "การสอบไล่", "การสืบทอดตำแหน่งพระมหากษัตริย์", "เป็นกฎระเบียบการขึ้นครองราชย์"},
                {"'บิดามารดา' เป็นคำทางการของใคร?", "พ่อแม่", "ปู่ย่า", "ตายาย", "ลูกๆ", "พ่อแม่", "ใช้ในเอกสารราชการ"},
                {"'บุพการี' หมายถึงใคร?", "บิดา มารดา ปู่ ย่า ตา ยาย (ผู้มีพระคุณสายตรง)", "ลูกหลาน", "เพื่อนร่วมงาน", "คนแปลกหน้า", "บิดา มารดา ปู่ ย่า ตา ยาย (ผู้มีพระคุณสายตรง)", "ผู้ที่ให้กำเนิดหรือเลี้ยงดูเรามา"},
                {"'ผู้สืบสันดาน' ลำดับที่ 1 คือใคร?", "บุตร (ลูก)", "หลาน", "เหลน", "พ่อแม่", "บุตร (ลูก)", "ในกฎหมายมรดก ลูกมีสิทธิก่อนหลาน"},
                {"'การรับรองบุตร' ทำเพื่ออะไร?", "เพื่อให้เด็กเป็นบุตรที่ชอบด้วยกฎหมายของพ่อ", "เพื่อความสนุก", "เพื่อชื่อเสียง", "เพื่อการค้า", "เพื่อให้เด็กเป็นบุตรที่ชอบด้วยกฎหมายของพ่อ", "สำคัญมากสำหรับพ่อที่ไม่ได้จดทะเบียนสมรสกับแม่"}
        };
    }
    private void seedForFamilyThai(TopicTest topicTest, LearningLanguage learningLanguage) {
        // TODO
        var es = ThaiFamilyEasy();
        var md = ThaiFamilyMedium();
        var h = ThaiFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] EnglishWorkEasy() {
        return new String[][] {
                {"My Career", "Mastering basic workplace titles and activities"},
                {"What do you call the person you work for?", "Boss / Manager", "Colleague", "Customer", "Intern", "Boss / Manager", "The person in charge of your department or tasks."},
                {"What is the document where you list your experience to get a job?", "Resume / CV", "Receipt", "Menu", "Passport", "Resume / CV", "In the US, it's a Resume; in the UK and Europe, it's often called a CV (Curriculum Vitae)."},
                {"What do you call the money you receive every month for your work?", "Salary", "Bonus", "Tax", "Debt", "Salary", "A salary is a fixed amount, while 'wages' often refer to hourly pay."},
                {"If you work more than the usual 40 hours a week, what is it called?", "Overtime (OT)", "Break", "Holiday", "Shift", "Overtime (OT)", "Often abbreviated as OT, and sometimes paid at a higher rate."},
                {"What is a 'Meeting'?", "A gathering to discuss work", "A lunch break", "A job interview", "A vacation", "A gathering to discuss work", "Meetings can be in-person or virtual (Zoom/Teams)."},
                {"What do you call the people you work with at the same level?", "Colleagues / Co-workers", "Subordinates", "Employers", "Clients", "Colleagues / Co-workers", "Your team members are your colleagues."},
                {"What do you do during a 'Job Interview'?", "Answer questions to get a job", "Buy new clothes", "Go on a trip", "Quit your job", "Answer questions to get a job", "It is a formal conversation between an applicant and an employer."},
                {"What is an 'Intern'?", "A student or trainee working for experience", "The CEO", "A retired person", "A regular customer", "A student or trainee working for experience", "Internships are common for gaining early-career experience."},
                {"What does it mean to 'Resign'?", "To quit your job voluntarily", "To get a promotion", "To start a business", "To take a nap", "To quit your job voluntarily", "You usually give a 'notice period' (e.g., two weeks) before resigning."},
                {"Where is the place most people go to work?", "The office", "The cinema", "The gym", "The park", "The office", "Modern offices can be physical or 'coworking' spaces."},
                {"What is a 'Full-time' job?", "Working a standard number of hours (e.g., 40/week)", "Working 2 hours a week", "Working for free", "Working only on weekends", "Working a standard number of hours (e.g., 40/week)", "Full-time employees usually get benefits like health insurance."}
        };
    }
    public static String[][] EnglishWorkMedium() {
        return new String[][] {
                {"Office Culture & Skills", "Understanding idioms and professional dynamics"},
                {"What is a 'Deadline'?", "The latest time to finish a task", "A type of phone line", "A lunch time", "A company rule", "The latest time to finish a task", "Missing a deadline can have negative consequences for a project."},
                {"What does 'WFH' stand for?", "Work From Home", "Wait For Help", "World Food House", "Work For Him", "Work From Home", "This became very popular after the 2020 pandemic."},
                {"What is 'Networking'?", "Building professional relationships", "Fixing computers", "Working late", "Applying for jobs", "Building professional relationships", "The saying goes: 'Your network is your net worth.'"},
                {"What do you call a formal talk given to an audience about a project?", "Presentation", "Gossip", "Interview", "Memo", "Presentation", "Usually involves slides (like PowerPoint) and a speech."},
                {"What is a 'Promotion'?", "Moving to a higher position with more pay", "Getting fired", "Buying a new car", "Taking a vacation", "Moving to a higher position with more pay", "It's the reward for good performance and more responsibility."},
                {"What does it mean to 'Brainstorm'?", "To generate many ideas quickly", "To complain about the weather", "To fix a roof", "To cancel a meeting", "To generate many ideas quickly", "Common in creative or problem-solving sessions."},
                {"What are 'Soft Skills'?", "Personal traits like communication and teamwork", "Technical skills like coding", "Lifting heavy objects", "Computer hardware", "Personal traits like communication and teamwork", "These are often harder to teach than 'hard skills'."},
                {"What is a 'Performance Review'?", "A meeting to evaluate your work", "A movie at the office", "A music concert", "A lunch party", "A meeting to evaluate your work", "Usually happens once or twice a year with your manager."},
                {"What is 'Micromanagement'?", "A boss controlling every small detail", "Managing a small team", "Using a microscope", "Small business accounting", "A boss controlling every small detail", "Most employees dislike being micromanaged."},
                {"What do you call a 'Work-life balance'?", "The healthy split between work and personal life", "Working 24/7", "Exercising at work", "Living in the office", "The healthy split between work and personal life", "Crucial for preventing 'burnout'."}
        };
    }public static String[][] EnglishWorkHard() {
        return new String[][] {
                {"Strategy & Professional Law", "Advanced terms for the business world"},
                {"What is 'Outsourcing'?", "Hiring an external party to do work", "Hiring a family member", "Moving the office", "Closing the company", "Hiring an external party to do work", "Companies outsource to save costs or use specialized talent."},
                {"What does 'ROI' stand for?", "Return on Investment", "Rules of Interview", "Right of Income", "Ratio of Industry", "Return on Investment", "A measure used to evaluate the efficiency of an investment."},
                {"What is a 'Non-disclosure Agreement' (NDA)?", "A contract to keep secrets", "A job offer", "A tax form", "A health insurance plan", "A contract to keep secrets", "Used to protect proprietary information or trade secrets."},
                {"What is 'Headhunting'?", "Recruiting top talent from other companies", "A scary movie", "A medical term", "A sports game", "Recruiting top talent from other companies", "Headhunters are professional recruiters for high-level roles."},
                {"What is 'Burnout'?", "Physical or mental collapse from overwork", "Fire in the kitchen", "Ending a shift early", "Losing a job", "Physical or mental collapse from overwork", "Symptoms include exhaustion and reduced performance."},
                {"What is 'Severance Pay'?", "Money paid to an employee when they are laid off", "A Christmas bonus", "A daily allowance", "Insurance for cars", "Money paid to an employee when they are laid off", "It helps support the worker while they look for a new job."},
                {"What does 'Scaling' a business mean?", "Growing a company significantly", "Climbing a ladder", "Cleaning the office", "Selling the company", "Growing a company significantly", "To scale means to increase revenue faster than costs."},
                {"What is a 'Stakeholder'?", "Anyone affected by the company's actions", "The owner only", "A meat chef", "A competitor", "Anyone affected by the company's actions", "Includes employees, customers, investors, and the community."},
                {"What is the 'Glass Ceiling'?", "An invisible barrier preventing women/minorities from rising", "A fancy office roof", "A cleaning service", "A transparent business model", "An invisible barrier preventing women/minorities from rising", "A metaphor used to describe systemic discrimination in promotion."},
                {"What is 'Agile' methodology?", "A flexible and iterative way to manage projects", "A fast running style", "A strictly scheduled plan", "A type of software", "A flexible and iterative way to manage projects", "Popular in software development, focusing on 'Sprints'."}
        };
    }
    private void seedForWorkEnglish(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = EnglishWorkEasy();
        var md = EnglishWorkMedium();
        var h = EnglishWorkHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] VietnameseWorkEasy() {
        return new String[][] {
                {"Nghề nghiệp của tôi", "Làm quen với các chức danh và hoạt động công sở cơ bản"},
                {"Người đứng đầu cao nhất của một công ty thường được gọi là gì?", "Giám đốc điều hành (CEO)", "Thư ký", "Nhân viên thực tập", "Bảo vệ", "Giám đốc điều hành (CEO)", "CEO là người chịu trách nhiệm quản lý tổng thể một tổ chức."},
                {"Nơi mọi người đến làm việc hàng ngày gọi là gì?", "Văn phòng", "Công viên", "Rạp chiếu phim", "Bệnh viện", "Văn phòng", "Văn phòng là không gian làm việc phổ biến của nhân viên hành chính."},
                {"Người hướng dẫn và quản lý trực tiếp công việc của bạn gọi là gì?", "Cấp trên / Sếp", "Đồng nghiệp", "Khách hàng", "Đối tác", "Cấp trên / Sếp", "Sếp là người phân công và đánh giá kết quả công việc của bạn."},
                {"Hoạt động giới thiệu bản thân để xin việc gọi là gì?", "Phỏng vấn", "Đi du lịch", "Mua sắm", "Nghỉ phép", "Phỏng vấn", "Đây là bước quan trọng để nhà tuyển dụng đánh giá ứng viên."},
                {"Người làm việc cùng cấp bậc với bạn trong công ty gọi là gì?", "Đồng nghiệp", "Cấp dưới", "Giám đốc", "Cố vấn", "Đồng nghiệp", "Đồng nghiệp là những người cùng sát cánh làm việc với bạn."},
                {"Bản tóm tắt quá trình học tập và kinh nghiệm làm việc gọi là gì?", "Sơ yếu lý lịch (CV)", "Hợp đồng", "Hóa đơn", "Bưu thiếp", "Sơ yếu lý lịch (CV)", "CV là 'tấm vé' giúp bạn tiếp cận nhà tuyển dụng."},
                {"Khoản tiền bạn nhận được hàng tháng từ công ty gọi là gì?", "Lương", "Tiền phạt", "Tiền mừng", "Tiền vay", "Lương", "Lương là thù lao cho công sức lao động của bạn."},
                {"Làm việc quá giờ quy định thường được gọi tắt là gì?", "OT (Overtime)", "Off", "Break", "Meeting", "OT (Overtime)", "Làm thêm giờ thường được trả lương cao hơn mức bình thường."},
                {"Người mới vào làm để học hỏi kinh nghiệm gọi là gì?", "Thực tập sinh", "Chuyên gia", "Trưởng phòng", "Cố vấn", "Thực tập sinh", "Thực tập là giai đoạn bước đệm cho sinh viên hoặc người mới."},
                {"Khi bạn muốn nghỉ việc vĩnh viễn, bạn phải viết đơn gì?", "Đơn xin nghỉ việc / Thôi việc", "Đơn xin nghỉ phép", "Đơn khiếu nại", "Đơn đặt hàng", "Đơn xin nghỉ việc / Thôi việc", "Bạn cần thông báo trước một thời hạn nhất định theo hợp đồng."},
                {"Cuộc họp để thảo luận công việc gọi là gì?", "Meeting / Buổi họp", "Party", "Workshop", "Teambuilding", "Meeting / Buổi họp", "Họp hành giúp thống nhất mục tiêu và giải quyết vấn đề."}
        };
    }public static String[][] VietnameseWorkMedium() {
        return new String[][] {
                {"Văn hóa & Quy trình", "Hiểu về các quy định và kỹ năng trong môi trường chuyên nghiệp"},
                {"'Thời hạn cuối cùng' để hoàn thành một công việc gọi là gì?", "Deadline", "Timeline", "Target", "KPI", "Deadline", "Chạy deadline là áp lực thường thấy trong công việc."},
                {"Khoản tiền thưởng thêm ngoài lương cơ bản gọi là gì?", "Bonus / Tiền thưởng", "Trợ cấp", "Phí cầu đường", "Tiền lãi", "Bonus / Tiền thưởng", "Thường dựa trên hiệu suất công việc hoặc các dịp lễ."},
                {"Hoạt động tập thể nhằm gắn kết nhân viên gọi là gì?", "Teambuilding", "Họp báo", "Đình công", "Khánh thành", "Teambuilding", "Giúp cải thiện mối quan hệ và tinh thần làm việc nhóm."},
                {"Chế độ làm việc mà nhân viên không cần đến văn phòng gọi là gì?", "Làm việc từ xa (Remote/WFH)", "Làm ca đêm", "Làm bán thời gian", "Làm việc tự do", "Làm việc từ xa (Remote/WFH)", "Viết tắt của Work From Home."},
                {"KPI là viết tắt của chỉ số gì trong công việc?", "Chỉ số đánh giá hiệu quả công việc", "Chỉ số thông minh", "Chỉ số hạnh phúc", "Chỉ số giá tiêu dùng", "Chỉ số đánh giá hiệu quả công việc", "Key Performance Indicator giúp đo lường mức độ hoàn thành mục tiêu."},
                {"Việc thảo luận để đi đến thống nhất về giá cả hoặc điều kiện gọi là gì?", "Đàm phán", "Tranh cãi", "Tâm sự", "Thuyết trình", "Đàm phán", "Kỹ năng đàm phán rất quan trọng trong kinh doanh."},
                {"Khả năng làm chủ thời gian và sắp xếp công việc gọi là gì?", "Quản lý thời gian", "Quản lý nhân sự", "Quản lý rủi ro", "Quản lý tài chính", "Quản lý thời gian", "Giúp bạn làm việc hiệu quả và tránh căng thẳng."},
                {"Môi trường làm việc năng động, mới thành lập gọi là gì?", "Startup / Khởi nghiệp", "Tập đoàn đa quốc gia", "Cơ quan nhà nước", "Xưởng sản xuất", "Startup / Khởi nghiệp", "Thường có cấu trúc ít phân cấp và thay đổi nhanh."},
                {"Việc tạm dừng công việc để đi du lịch, nghỉ ngơi gọi là gì?", "Nghỉ phép năm", "Nghỉ thai sản", "Nghỉ ốm", "Nghỉ không lương", "Nghỉ phép năm", "Nhân viên chính thức thường có ít nhất 12 ngày phép/năm."},
                {"Quy tắc về trang phục khi đi làm gọi là gì?", "Dress code", "Barcode", "Zip code", "QR code", "Dress code", "Tùy công ty mà quy định trang phục trang trọng hay thoải mái."}
        };
    }public static String[][] VietnameseWorkHard() {
        return new String[][] {
                {"Quản trị & Pháp lý", "Thử thách với các thuật ngữ kinh tế và luật lao động"},
                {"Văn bản pháp lý cam kết quyền lợi giữa người lao động và người sử dụng lao động?", "Hợp đồng lao động", "Biên bản bàn giao", "Quyết định khen thưởng", "Nội quy công ty", "Hợp đồng lao động", "Đây là cơ sở pháp lý cao nhất bảo vệ quyền lợi hai bên."},
                {"Khoản tiền trích từ lương để chi trả cho y tế, hưu trí sau này?", "Bảo hiểm xã hội", "Thuế thu nhập", "Quỹ khuyến học", "Tiền công đoàn", "Bảo hiểm xã hội", "Bao gồm bảo hiểm y tế, bảo hiểm thất nghiệp và hưu trí."},
                {"Chế độ làm việc mà bạn làm chủ hoàn toàn về thời gian và khách hàng?", "Freelancer (Làm việc tự do)", "Nhân viên chính thức", "Công chức", "Hợp tác xã", "Freelancer (Làm việc tự do)", "Người làm tự do tự quản lý dự án và đóng thuế cá nhân."},
                {"Việc một công ty thuê một đơn vị bên ngoài làm một phần việc cho mình?", "Outsourcing (Thuê ngoài)", "M&A", "Headhunt", "Franchise", "Outsourcing (Thuê ngoài)", "Giúp doanh nghiệp tập trung vào thế mạnh cốt lõi."},
                {"Thuật ngữ chỉ việc cắt giảm nhân sự hàng loạt để tiết kiệm chi phí?", "Layoff", "Recruitment", "Onboarding", "Promotion", "Layoff", "Thường xảy ra khi kinh tế suy thoái hoặc tái cấu trúc."},
                {"Hành vi sử dụng chức vụ để trục lợi cá nhân gọi là gì?", "Tham nhũng", "Cống hiến", "Minh bạch", "Từ thiện", "Tham nhũng", "Đây là hành vi vi phạm pháp luật và đạo đức nghề nghiệp."},
                {"Người làm nhiệm vụ tìm kiếm và 'săn' nhân tài cho các vị trí cao cấp?", "Headhunter", "Shipper", "Broker", "Dealer", "Headhunter", "Họ đóng vai trò trung gian giữa ứng viên tài năng và doanh nghiệp."},
                {"Quy trình đào tạo nhân viên mới làm quen với công ty gọi là gì?", "Onboarding", "Offboarding", "Training", "Coaching", "Onboarding", "Giúp nhân viên mới hòa nhập nhanh chóng."},
                {"Hợp đồng ký kết giữa hai doanh nghiệp để cùng thực hiện dự án?", "Hợp đồng kinh tế / Hợp tác", "Hợp đồng lao động", "Hợp đồng thuê nhà", "Hợp đồng hôn nhân", "Hợp đồng kinh tế / Hợp tác", "Xác định quyền lợi, nghĩa vụ và chia sẻ rủi ro giữa các tổ chức."},
                {"Việc thăng tiến lên vị trí cao hơn trong sự nghiệp gọi là gì?", "Lộ trình thăng tiến (Career path)", "Nhảy việc", "Nghỉ hưu", "Đình trệ", "Lộ trình thăng tiến (Career path)", "Mỗi cá nhân cần xây dựng lộ trình để đạt mục tiêu dài hạn."}
        };
    }
    private void seedForWorkVietNam(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = VietnameseWorkEasy();
        var md = VietnameseWorkMedium();
        var h = VietnameseWorkHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] SpanishWorkEasy() {
        return new String[][] {
            {"Mi Trabajo", "Dominando el vocabulario básico de la oficina"},
            {"¿Cómo se llama la persona que te da órdenes en el trabajo?", "El jefe / La jefa", "El colega", "El cliente", "El becario", "El jefe / La jefa", "Es la persona responsable de liderar un equipo."},
            {"¿Qué documento usas para solicitar un empleo?", "El Currículum (CV)", "El pasaporte", "La factura", "La carta de amor", "El Currículum (CV)", "Es el resumen de tu educación y experiencia laboral."},
            {"¿Cómo se llama el dinero que recibes cada mes?", "El sueldo / salario", "La propina", "El impuesto", "La multa", "El sueldo / salario", "Es la remuneración económica por tu trabajo."},
            {"¿Dónde trabajan normalmente las personas con ordenadores?", "La oficina", "El gimnasio", "La playa", "El cine", "La oficina", "El lugar físico donde se desarrolla el trabajo administrativo."},
            {"¿Cómo llamas a las personas que trabajan contigo?", "Los compañeros / colegas", "Los enemigos", "Los jefes", "Los alumnos", "Los compañeros / colegas", "Son las personas con las que compartes el entorno laboral."},
            {"¿Qué haces en una 'Entrevista de trabajo'?", "Responder preguntas para conseguir empleo", "Dormir", "Comprar comida", "Bailar", "Responder preguntas para conseguir empleo", "Es el proceso de selección de una empresa."},
            {"¿Cómo se llama un estudiante que trabaja para ganar experiencia?", "El becario / pasante", "El director", "El dueño", "El jubilado", "El becario / pasante", "Realizan una 'pasantía' o 'beca' para aprender."},
            {"Si decides dejar tu trabajo voluntariamente, ¿qué haces?", "Renunciar / Dimitir", "Ascender", "Contratar", "Despedir", "Renunciar / Dimitir", "Es el acto de dejar el puesto de trabajo por voluntad propia."},
            {"¿Qué es una 'Reunión'?", "Un encuentro para discutir temas de trabajo", "Una fiesta de cumpleaños", "Una siesta", "Un viaje", "Un encuentro para discutir temas de trabajo", "Se pueden realizar de forma presencial o virtual."},
            {"¿Cómo se dice 'to hire' en español?", "Contratar", "Vender", "Romper", "Cantar", "Contratar", "Es el acto de dar empleo a una persona."},
            {"¿Qué recibes si trabajas más horas de lo normal?", "Horas extras", "Vacaciones", "Menos sueldo", "Un regalo", "Horas extras", "Suelen pagarse a un precio mayor que la hora normal."}
        };
    }
    private void seedForWorkSpanish(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = SpanishWorkEasy();
        var md = SpanishWorkEasy();
        var h = SpanishWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] FrenchWorkEasy() {
        return new String[][] {
                {"Mon Travail", "Maîtriser le vocabulaire de base du bureau"},
                {"Comment appelle-t-on la personne qui dirige une équipe ?", "Le patron / Le chef", "Le collègue", "Le stagiaire", "Le client", "Le patron / Le chef", "C'est la personne responsable de donner des instructions."},
                {"Quel document contient votre expérience et vos études ?", "Le CV", "Le passeport", "Le menu", "La facture", "Le CV", "Le Curriculum Vitae est essentiel pour postuler à un emploi."},
                {"Comment appelle-t-on l'argent que vous recevez chaque mois ?", "Le salaire", "Le pourboire", "L'impôt", "Le cadeau", "Le salaire", "C'est la rémunération pour votre travail."},
                {"Où travaillent généralement les employés de bureau ?", "Le bureau", "Le parc", "La cuisine", "La piscine", "Le bureau", "Lieu où l'on effectue des tâches administratives."},
                {"Comment appelle-t-on les personnes qui travaillent avec vous ?", "Les collègues", "Les patrons", "Les ennemis", "Les étudiants", "Les collègues", "Ce sont vos partenaires de travail au quotidien."},
                {"Que fait-on lors d'un 'Entretien d'embauche' ?", "On répond à des questions pour avoir un job", "On dort", "On mange au restaurant", "On chante", "On répond à des questions pour avoir un job", "C'est une étape clé du recrutement."},
                {"Comment appelle-t-on un étudiant qui travaille pour apprendre ?", "Un stagiaire", "Un directeur", "Un client", "Un expert", "Un stagiaire", "Le stage permet de découvrir le monde professionnel."},
                {"Si vous voulez quitter votre emploi, que faites-vous ?", "Démissionner", "Promouvoir", "Embaucher", "Acheter", "Démissionner", "C'est l'acte de rompre son contrat de travail volontairement."},
                {"Qu'est-ce qu'une 'Réunion' ?", "Une rencontre pour discuter du travail", "Une fête", "Une sieste", "Un voyage", "Une rencontre pour discuter du travail", "Les réunions peuvent être physiques ou en visioconférence."},
                {"Comment dit-on 'to hire' en français ?", "Embaucher", "Vendre", "Partir", "Écrire", "Embaucher", "C'est l'action de recruter un nouveau collaborateur."},
                {"Comment appelle-t-on le temps de pause au milieu de la journée ?", "La pause déjeuner", "La pause dodo", "Le petit déjeuner", "Le dîner", "La pause déjeuner", "En France, la pause déjeuner est un moment social important."}
        };
    }
    private void seedForWorkFrench(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = FrenchWorkEasy();
        var md = FrenchWorkEasy();
        var h = FrenchWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] GermanWorkEasy() {
        return new String[][] {
                {"Meine Arbeit", "Grundwortschatz für das Büro lernen"},
                {"Wie nennt man die Person, die ein Team leitet?", "Der Chef / Die Chefin", "Der Kollege", "Der Praktikant", "Der Kunde", "Der Chef / Die Chefin", "Die Führungskraft in einer Abteilung."},
                {"Welches Dokument enthält Informationen über Ihre Ausbildung?", "Der Lebenslauf", "Der Reisepass", "Die Quittung", "Die Speisekarte", "Der Lebenslauf", "Ein Lebenslauf ist für jede Bewerbung notwendig."},
                {"Wie nennt man das Geld, das man monatlich bekommt?", "Das Gehalt / Der Lohn", "Das Trinkgeld", "Die Steuer", "Die Strafe", "Das Gehalt / Der Lohn", "Das Entgelt für die geleistete Arbeit."},
                {"Wo arbeiten die meisten Büroangestellten?", "Das Büro", "Der Park", "Das Fitnessstudio", "Das Kino", "Das Büro", "Der Ort, an dem administrative Aufgaben erledigt werden."},
                {"Wie nennt man die Leute, mit denen man zusammenarbeitet?", "Die Kollegen", "Die Chefs", "Die Feinde", "Die Studenten", "Die Kollegen", "Menschen, die im gleichen Unternehmen arbeiten."},
                {"Was macht man bei einem 'Vorstellungsgespräch'?", "Fragen beantworten, um einen Job zu bekommen", "Schlafen", "Einkaufen", "Tanzen", "Fragen beantworten, um einen Job zu bekommen", "Das erste Treffen zwischen Bewerber und Arbeitgeber."},
                {"Wie nennt man einen Studenten, der lernt und arbeitet?", "Der Praktikant", "Der Direktor", "Der Besitzer", "Der Rentner", "Der Praktikant", "Ein Praktikum dient dem Sammeln von Erfahrung."},
                {"Wenn man freiwillig mit der Arbeit aufhört, was tut man?", "Kündigen", "Befördert werden", "Einstellen", "Kaufen", "Kündigen", "Den Arbeitsvertrag einseitig beenden."},
                {"Was ist ein 'Meeting' oder eine 'Besprechung'?", "Ein Treffen, um über die Arbeit zu reden", "Eine Geburtstagsparty", "Ein Nickerchen", "Eine Reise", "Ein Treffen, um über die Arbeit zu reden", "Besprechungen können persönlich oder online stattfinden."},
                {"Wie sagt man 'to hire' auf Deutsch?", "Einstellen", "Verkaufen", "Verlassen", "Schreiben", "Einstellen", "Einen neuen Mitarbeiter unter Vertrag nehmen."},
                {"Was bekommt man, wenn man mehr als normal arbeitet?", "Überstunden", "Urlaub", "Weniger Geld", "Ein Geschenk", "Überstunden", "Zusätzliche Arbeitszeit über das normale Maß hinaus."}
        };
    }
    private void seedForWorkGerman(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = GermanWorkEasy();
        var md = GermanWorkEasy();
        var h = GermanWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ChineseWorkEasy() {
        return new String[][] {
                {"我的工作", "学习办公室基础词汇"},
                {"在公司里管理团队的人叫什么？", "老板 / 经理", "同事", "实习生", "保安", "老板 / 经理", "经理 (Jīnglǐ) 负责管理部门或团队。"},
                {"申请工作时用的个人简历叫什么？", "简历", "护照", "发票", "明信片", "简历", "简历 (Jiǎnlì) 是展示经验和技能的文件。"},
                {"每个月工作的报酬叫什么？", "工资 / 薪水", "奖金", "税", "小费", "工资 / 薪水", "工资 (Gōngzī) 是劳动的报酬。"},
                {"大多数办公室职员在哪里工作？", "办公室", "公园", "健身房", "电影院", "办公室", "办公室 (Bàngōngshì) 是行政办公场所。"},
                {"和你一起工作的人叫什么？", "同事", "老板", "敌人", "学生", "同事", "同事 (Tóngshì) 是在同一家公司工作的人。"},
                {"为了得到工作进行的正式谈话叫什么？", "面试", "睡觉", "买东西", "跳舞", "面试", "面试 (Miànshì) 是招聘的重要环节。"},
                {"在公司学习经验的学生叫什么？", "实习生", "导演", "主人", "退休人员", "实习生", "实习生 (Shíxíshēng) 正在进行职业实践。"},
                {"主动离开工作岗位的行为叫什么？", "辞职", "升职", "入职", "买东西", "辞职", "辞职 (Cízhí) 是员工主动解除劳动合同。"},
                {"大家聚在一起讨论工作的活动叫什么？", "开会", "生日派对", "午睡", "旅游", "开会", "开会 (Kāihuì) 用于沟通和解决问题。"},
                {"中文里 'to hire' 怎么说？", "招聘 / 录用", "卖", "离开", "写", "招聘 / 录用", "招聘 (Zhāopìn) 是寻找和雇佣员工的过程。"},
                {"工作超过规定时间叫什么？", "加班", "放假", "扣钱", "送礼", "加班", "加班 (Jiābān) 在中国职场非常普遍。"}
        };
    }
    private void seedForWorkChinese(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = ChineseWorkEasy();
        var md = ChineseWorkEasy();
        var h = ChineseWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] JapaneseWorkEasy() {
        return new String[][] {
            {"私の仕事", "オフィスでの基本単語を学ぶ"},
            {"会社でチームを管理する人を何と言いますか？", "上司 / マネージャー", "同僚", "インターン", "警備員", "上司 / マネージャー", "上司 (Jōshi) はチームを指導する責任者です。"},
            {"仕事に応募するときに使う書類は何ですか？", "履歴書", "パスポート", "領収書", "ポストカード", "履歴書", "履歴書 (Rirekisho) は自分の経歴を伝える大切な書類です。"},
            {"毎月もらう仕事の報酬を何と言いますか？", "給料", "ボーナス", "税金", "チップ", "給料", "給料 (Kyūryō) は働いた対価として支払われます。"},
            {"ほとんどの会社員はどこで働いていますか？", "事務所 / オフィス", "公園", "ジム", "映画館", "事務所 / オフィス", "事務所 (Jimusho) は事務仕事を行う場所です。"},
            {"一緒に働いている人を何と言いますか？", "同僚", "ボス", "敵", "学生", "同僚", "同僚 (Dōryō) は同じ職場で働く仲間です。"},
            {"仕事を得るための正式な話し合いを何と言いますか？", "面接", "睡眠", "買い物", "ダンス", "面接", "面接 (Mensetsu) は採用の重要なステップです。"},
            {"会社で経験を積んでいる学生を何と言いますか？", "インターン", "監督", "オーナー", "引退者", "インターン", "インターンシップ (Internship) を通じて実務を学びます。"},
            {"自分の意志で仕事を辞めることを何と言いますか？", "退職 / 辞職", "昇進", "採用", "買い物", "退職 / 辞職", "会社に辞表を提出して仕事を辞めることです。"},
            {"仕事について話し合う集まりを何と言いますか？", "会議 / ミーティング", "パーティー", "昼寝", "旅行", "会議 / ミーティング", "会議 (Kaigi) で問題解決や情報共有を行います。"},
            {"日本語で 'to hire' は何と言いますか？", "採用する / 雇う", "売る", "去る", "書く", "採用する / 雇う", "新しい人をチームに迎えることです。"},
            {"決められた時間を超えて働くことを何と言いますか？", "残業", "休み", "減給", "プレゼント", "残業", "残業 (Zangyō) は日本の職場でよく見られる現象です。"}
        };
    }
    private void seedForWorkJapanese(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = JapaneseWorkEasy();
        var md = JapaneseWorkEasy();
        var h = JapaneseWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] KoreanWorkEasy() {
        return new String[][] {
                {"나의 직장", "사무실 기초 단어 배우기"},
                {"회사에서 팀을 관리하는 사람을 무엇이라고 합니까?", "상사 / 매니저", "동료", "인턴", "경비원", "상사 / 매니저", "상사 (Sang-sa)는 나보다 직급이 높은 사람을 뜻합니다."},
                {"입사 지원을 할 때 쓰는 서류는 무엇입니까?", "이력서", "여권", "영수증", "엽서", "이력서", "이력서 (I-ryeok-seo)에는 학력과 경력을 적습니다."},
                {"매달 받는 일의 대가를 무엇이라고 합니까?", "월급 / 급여", "보너스", "세금", "팁", "월급 / 급여", "월급 (Wol-geup)은 일한 보상으로 받는 돈입니다."},
                {"대부분의 직장인은 어디에서 일합니까?", "사무실", "공원", "체육관", "영화관", "사무실", "사무실 (Sa-mu-sil)은 업무를 보는 공간입니다."},
                {"함께 일하는 사람을 무엇이라고 합니까?", "동료", "사장", "적", "학생", "동료", "동료 (Dong-ryo)는 같은 직장에서 일하는 동료입니다."},
                {"직원을 뽑기 위해 공식적으로 대화하는 것을 무엇이라고 합니까?", "면접", "수면", "쇼핑", "댄스", "면접", "면접 (Myeon-jeop)은 취업의 중요한 단계입니다."},
                {"회사에서 실무를 배우는 학생을 무엇이라고 합니까?", "인턴", "감독", "주인", "은퇴자", "인턴", "인턴 (In-teon) 기간을 통해 업무 경험을 쌓습니다."},
                {"스스로 일을 그만두는 것을 무엇이라고 합니까?", "퇴사 / 사직", "승진", "채용", "쇼핑", "퇴사 / 사직", "퇴사 (Toe-sa)는 회사를 떠나는 것입니다."},
                {"업무를 논의하기 위해 모이는 것을 무엇이라고 합니까?", "회의", "생일 파티", "낮잠", "여행", "회의", "회의 (Hoe-ui)를 통해 의견을 나눕니다."},
                {"한국어로 'to hire'는 무엇입니까?", "채용하다 / 고용하다", "팔다", "떠나다", "쓰다", "채용하다 / 고용하다", "회사가 새로운 사람을 뽑는 것입니다."},
                {"정해진 시간보다 더 일하는 것을 무엇이라고 합니까?", "야근", "휴가", "감봉", "선물", "야근", "야근 (Ya-geun)은 밤늦게까지 일하는 것을 뜻합니다."}
        };
    }
    private void seedForWorkKorean(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = KoreanWorkEasy();
        var md = KoreanWorkEasy();
        var h = KoreanWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] HindiWorkEasy() {
        return new String[][] {
                {"मेरा काम", "कार्यालय के बुनियादी शब्द सीखें"},
                {"कंपनी में टीम का प्रबंधन करने वाले व्यक्ति को क्या कहते हैं?", "बॉस / मैनेजर", "सहकर्मी", "इंटर्न", "गार्ड", "बॉस / मैनेजर", "मैनेजर (Manager) टीम को दिशा देने का काम करता है।"},
                {"नौकरी के लिए आवेदन करते समय अनुभव दिखाने वाला दस्तावेज़ क्या है?", "रिज्यूमे / सीवी", "पासपोर्ट", "रसीद", "मेन्यू", "रिज्यूमे / सीवी", "रिज्यूमे (Resume) में आपकी शिक्षा और कार्य अनुभव की जानकारी होती है।"},
                {"काम के बदले हर महीने मिलने वाले पैसे को क्या कहते हैं?", "वेतन / सैलरी", "बोनस", "टैक्स", "उधार", "वेतन / सैलरी", "वेतन (Salary) आपके श्रम का मासिक मुआवजा है।"},
                {"ज्यादातर ऑफिस कर्मचारी कहाँ काम करते हैं?", "दफ्तर / ऑफिस", "पार्क", "जिम", "सिनेमा", "दफ्तर / ऑफिस", "दफ्तर (Office) वह जगह है जहाँ प्रशासनिक कार्य होते हैं।"},
                {"आपके साथ काम करने वाले लोगों को क्या कहते हैं?", "सहकर्मी", "मालिक", "दुश्मन", "छात्र", "सहकर्मी", "सहकर्मी (Colleagues) वे लोग हैं जो आपके साथ काम करते हैं।"},
                {"नौकरी पाने के लिए होने वाली औपचारिक बातचीत को क्या कहते हैं?", "इंटरव्यू", "नींद", "खरीदारी", "डांस", "इंटरव्यू", "इंटरव्यू (Interview) चयन प्रक्रिया का एक महत्वपूर्ण हिस्सा है।"},
                {"अनुभव प्राप्त करने के लिए काम करने वाले छात्र को क्या कहते हैं?", "इंटर्न", "निर्देशक", "मालिक", "रिटायर्ड", "इंटर्न", "इंटर्न (Intern) वास्तविक कार्य वातावरण में सीखना शुरू करते हैं।"},
                {"अपनी मर्जी से नौकरी छोड़ने को क्या कहते हैं?", "इस्तीफा देना", "प्रमोशन", "भर्ती", "छुट्टी", "इस्तीफा देना", "इस्तीफा (Resign) देना मतलब स्वेच्छा से पद छोड़ना।"},
                {"काम के बारे में चर्चा करने के लिए होने वाली बैठक को क्या कहते हैं?", "मीटिंग", "पार्टी", "झपकी", "यात्रा", "मीटिंग", "मीटिंग (Meeting) में विचारों और योजनाओं पर चर्चा होती है।"},
                {"हिंदी में 'to hire' को क्या कहते हैं?", "भर्ती करना", "बेचना", "छोड़ना", "लिखना", "भर्ती करना", "भर्ती (Hire) करना मतलब नए कर्मचारी को काम पर रखना।"},
                {"तय समय से ज्यादा काम करने को क्या कहते हैं?", "ओवरटाइम", "ब्रेक", "त्योहार", "शिफ्ट", "ओवरटाइम", "ओवरटाइम (Overtime) का मतलब है अतिरिक्त घंटों के लिए काम करना।"}
        };
    }
    private void seedForWorkHindi(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = HindiWorkEasy();
        var md = HindiWorkEasy();
        var h = HindiWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] RussianWorkEasy() {
        return new String[][] {
                {"Моя работа", "Учим базовые слова для офиса"},
                {"Как называют человека, который управляет командой?", "Начальник / Босс", "Коллега", "Стажер", "Охранник", "Начальник / Босс", "Начальник (Nachalnik) несет ответственность за работу отдела."},
                {"Как называется документ с вашим опытом работы?", "Резюме", "Паспорт", "Чек", "Открытка", "Резюме", "Резюме (Rezyume) необходимо для поиска работы."},
                {"Как называются деньги, которые вы получаете каждый месяц?", "Зарплата", "Бонус", "Налог", "Штраф", "Зарплата", "Зарплата (Zarplata) — это вознаграждение за труд."},
                {"Где обычно работают офисные сотрудники?", "Офис", "Парк", "Спортзал", "Кинотеатр", "Офис", "Офис (Ofis) — место для административной работы."},
                {"Как называют людей, которые работают вместе с вами?", "Коллеги", "Боссы", "Враги", "Студенты", "Коллеги", "Коллеги (Kollegi) — это ваши сотрудники по работе."},
                {"Что вы делаете на 'Собеседовании'?", "Отвечаете на вопросы, чтобы получить работу", "Спите", "Покупаете еду", "Танцуете", "Отвечаете на вопросы, чтобы получить работу", "Собеседование (Sobesedovaniye) — важный этап найма."},
                {"Как называют студента, который учится и работает для опыта?", "Стажер", "Директор", "Владелец", "Пенсионер", "Стажер", "Стажировка помогает получить первый опыт работы."},
                {"Как сказать 'уйти с работы по собственному желанию'?", "Уволиться", "Повыситься", "Наняться", "Купить", "Уволиться", "Увольнение (Uvol'neniye) — это прекращение трудового договора."},
                {"Как называется встреча для обсуждения работы?", "Собрание / Митинг", "Вечеринка", "Сон", "Путешествие", "Собрание / Митинг", "На собрании обсуждаются планы и задачи."},
                {"Как сказать 'нанять на работу' по-русски?", "Нанять", "Продать", "Уйти", "Написать", "Нанять", "Нанять (Nanyat') — значит принять нового сотрудника."},
                {"Что вы получаете, если работаете больше нормы?", "Переработка / Сверхурочные", "Отпуск", "Штраф", "Подарок", "Переработка / Сверхурочные", "В России сверхурочная работа должна оплачиваться дополнительно."}
        };
    }
    private void seedForWorkRussian(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = RussianWorkEasy();
        var md = RussianWorkEasy();
        var h = RussianWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] PortugueseWorkEasy() {
        return new String[][] {
                {"Meu Trabalho", "Aprender o vocabulário básico de escritório"},
                {"Como se chama a pessoa que gerencia uma equipe?", "O chefe / O gerente", "O colega", "O estagiário", "O segurança", "O chefe / O gerente", "O chefe é o responsável por guiar a equipe."},
                {"Qual documento contém sua experiência e educação?", "O Currículo (CV)", "O passaporte", "A fatura", "O cartão postal", "O Currículo (CV)", "O currículo é essencial para conseguir uma entrevista."},
                {"Como se chama o dinheiro que você recebe todo mês?", "O salário", "A gorjeta", "O imposto", "A multa", "O salário", "O salário é a remuneração pelo seu tempo de trabalho."},
                {"Onde a maioria dos funcionários de escritório trabalha?", "No escritório", "No parque", "Na academia", "No cinema", "No escritório", "O escritório é o local físico das atividades administrativas."},
                {"Como você chama as pessoas que trabalham com você?", "Os colegas", "Os patrões", "Os inimigos", "Os alunos", "Os colegas", "Colegas de trabalho são seus parceiros no dia a dia."},
                {"O que você faz em uma 'Entrevista de emprego'?", "Responde perguntas para conseguir a vaga", "Dorme", "Compra comida", "Dança", "Responde perguntas para conseguir a vaga", "É o momento de apresentar suas qualidades ao empregador."},
                {"Como se chama um estudante que trabalha para aprender?", "O estagiário", "O diretor", "O dono", "O aposentado", "O estagiário", "O estágio é a porta de entrada para muitos jovens."},
                {"Se você decide sair do trabalho voluntariamente, o que faz?", "Pedir demissão", "Ser promovido", "Contratar", "Comprar", "Pedir demissão", "É o ato de encerrar o contrato por vontade própria."},
                {"O que é uma 'Reunião'?", "Um encontro para discutir o trabalho", "Uma festa", "Uma soneca", "Uma viagem", "Um encontro para discutir o trabalho", "As reuniões servem para alinhar metas e resolver problemas."},
                {"Como se diz 'to hire' em português?", "Contratar", "Vender", "Sair", "Escrever", "Contratar", "É o ato de admitir um novo funcionário na empresa."},
                {"O que você recebe se trabalhar mais horas que o normal?", "Horas extras", "Férias", "Menos salário", "Um presente", "Horas extras", "As horas extras devem ser pagas com um acréscimo legal."}
        };
    }
    private void seedForWorkPortuguese(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = PortugueseWorkEasy();
        var md = PortugueseWorkEasy();
        var h = PortugueseWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ArabicWorkEasy() {
        return new String[][] {
                {"عملي", "تعلم المفردات الأساسية في المكتب"},
                {"ماذا تسمي الشخص الذي يدير فريقاً؟", "المدير / المدير العام", "الزميل", "المتدرب", "الحارس", "المدير / المدير العام", "المدير (Al-mudeer) هو المسؤول عن توجيه الفريق."},
                {"ما هي الوثيقة التي تحتوي على خبرتك وتعليمك؟", "السيرة الذاتية (CV)", "جواز السفر", "الفاتورة", "البطاقة البريدية", "السيرة الذاتية (CV)", "السيرة الذاتية هي المفتاح للحصول على مقابلة عمل."},
                {"ماذا تسمى الأموال التي تتلقاها كل شهر؟", "الراتب", "البقشيش", "الضريبة", "الدين", "الراتب", "الراتب (Ar-ratib) هو الأجر مقابل عملك."},
                {"أين يعمل معظم موظفي المكاتب؟", "المكتب", "الحديقة", "النادي الرياضي", "السينما", "المكتب", "المكتب (Al-maktab) هو المكان المخصص للعمل الإداري."},
                {"ماذا تسمي الأشخاص الذين يعملون معك؟", "الزملاء", "الرؤساء", "الأعداء", "الطلاب", "الزملاء", "زملاء العمل هم شركاؤك في المهام اليومية."},
                {"ماذا تفعل في 'مقابلة العمل'؟", "تجيب على أسئلة للحصول على الوظيفة", "تنام", "تشتري الطعام", "ترقص", "تجيب على أسئلة للحصول على الوظيفة", "المقابلة (Muqabalah) هي فرصة لعرض مهاراتك."},
                {"ماذا يسمى الطالب الذي يعمل ليتعلم؟", "متدرب", "مدير", "صاحب العمل", "متقاعد", "متدرب", "التدريب هو الخطوة الأولى في الحياة المهنية."},
                {"إذا قررت ترك العمل طواعية، ماذا تفعل؟", "تستقيل", "تترقى", "توظف", "تشتري", "تستقيل", "الاستقالة (Istiqalah) هي إنهاء عقد العمل برغبتك."},
                {"ما هو 'الاجتماع'؟", "لقاء لمناقشة العمل", "حفلة", "قيلولة", "رحلة", "لقاء لمناقشة العمل", "الاجتماعات (Ijtima'at) تُستخدم لتنسيق الأهداف."},
                {"كيف تقول 'To hire' باللغة العربية؟", "يوظف", "يبيع", "يغادر", "يكتب", "يوظف", "التوظيف هو اختيار موظف جديد للشركة."},
                {"ماذا تحصل عليه إذا عملت ساعات أكثر من المعتاد؟", "العمل الإضافي", "عطلة", "خصم", "هدية", "العمل الإضافي", "العمل الإضافي (Overtime) يجب أن يكون مأجوراً."}
        };
    }
    private void seedForWorkArabic(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = ArabicWorkEasy();
        var md = ArabicWorkEasy();
        var h = ArabicWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] ItalianWorkEasy() {
        return new String[][] {
                {"Il mio lavoro", "Imparare il vocabolario di base per l'ufficio"},
                {"Come si chiama la persona che dirige un team?", "Il capo / Il dirigente", "Il collega", "Lo stagista", "La guardia", "Il capo / Il dirigente", "Il capo è responsabile del coordinamento del team."},
                {"Quale documento contiene la tua esperienza e istruzione?", "Il Curriculum (CV)", "Il passaporto", "La ricevuta", "La cartolina", "Il Curriculum (CV)", "Il CV è fondamentale per presentarsi a un'azienda."},
                {"Come si chiama il denaro che ricevi ogni mese?", "Lo stipendio", "La mancia", "La tassa", "La multa", "Lo stipendio", "Lo stipendio è il compenso per il tuo lavoro."},
                {"Dove lavorano solitamente gli impiegati?", "In ufficio", "Al parco", "In palestra", "Al cinema", "In ufficio", "L'ufficio è il luogo fisico delle attività amministrative."},
                {"Come chiami le persone che lavorano con te?", "I colleghi", "I padroni", "I nemici", "Gli studenti", "I colleghi", "I colleghi sono i tuoi compagni di lavoro quotidiano."},
                {"Cosa fai in un 'Colloquio di lavoro'?", "Rispondi a domande per ottenere il posto", "Dormi", "Compri cibo", "Balli", "Rispondi a domande per ottenere il posto", "È il momento della selezione del personale."},
                {"Come si chiama uno studente che lavora per imparare?", "Lo stagista", "Il direttore", "Il proprietario", "Il pensionato", "Lo stagista", "Lo stage o tirocinio serve per fare esperienza."},
                {"Se decidi di lasciare il lavoro volontariamente, cosa fai?", "Dimettersi", "Essere promosso", "Assumere", "Comprare", "Dimettersi", "Dare le dimissioni significa chiudere il contratto."},
                {"Che cos'è una 'Riunione'?", "Un incontro per discutere di lavoro", "Una festa", "Un pisolino", "Un viaggio", "Un incontro per discutere di lavoro", "Le riunioni servono per pianificare le attività."},
                {"Come si dice 'to hire' in italiano?", "Assumere", "Vendere", "Partire", "Scrivere", "Assumere", "Significa inserire un nuovo lavoratore in azienda."},
                {"Cosa ricevi se lavori più ore del normale?", "Straordinari", "Ferie", "Meno stipendio", "Un regalo", "Straordinari", "Le ore straordinarie sono pagate con una maggiorazione."}
        };
    }
    private void seedForWorkItalian(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = ItalianWorkEasy();
        var md = ItalianWorkEasy();
        var h = ItalianWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] TurkishWorkEasy() {
        return new String[][] {
                {"İşim", "Ofis için temel kelimeleri öğrenin"},
                {"Bir ekibi yöneten kişiye ne denir?", "Patron / Müdür", "Meslektaş", "Stajyer", "Güvenlik", "Patron / Müdür", "Müdür (Müdür) ekibi yönlendirmekten sorumludur."},
                {"Deneyiminizi ve eğitiminizi gösteren belge nedir?", "Özgeçmiş (CV)", "Pasaport", "Fatura", "Kartpostal", "Özgeçmiş (CV)", "İş başvurusu için bir özgeçmiş hazırlamanız gerekir."},
                {"Her ay aldığınız paraya ne denir?", "Maaş", "Bahşiş", "Vergi", "Borç", "Maaş", "Maaş (Maaş) emeğinizin karşılığıdır."},
                {"Ofis çalışanları genellikle nerede çalışır?", "Ofis", "Park", "Spor salonu", "Sinema", "Ofis", "Ofis (Ofis) idari işlerin yapıldığı yerdir."},
                {"Sizinle birlikte çalışan kişilere ne denir?", "İş arkadaşları / Meslektaşlar", "Patronlar", "Düşmanlar", "Öğrenciler", "İş arkadaşları / Meslektaşlar", "Meslektaşlar (Meslektaş) aynı iş yerindeki ortaklarınızdır."},
                {"İş almak için yapılan resmi görüşmeye ne denir?", "Mülakat", "Uyku", "Alışveriş", "Dans", "Mülakat", "Mülakat (Mülakat) işe alım sürecinin önemli bir parçasıdır."},
                {"Öğrenmek için çalışan öğrenciye ne denir?", "Stajyer", "Direktör", "Sahip", "Emekli", "Stajyer", "Stajyerlik mesleki hayata ilk adımdır."},
                {"Kendi isteğinizle işten ayrılmaya ne denir?", "İstifa etmek", "Terfi etmek", "İşe almak", "Satın almak", "İstifa etmek", "İstifa (İstifa) iş sözleşmesini sonlandırmaktır."},
                {"İş hakkında konuşmak için yapılan toplantıya ne denir?", "Toplantı", "Parti", "Şekerleme", "Seyahat", "Toplantı", "Toplantıda (Toplantı) planlar ve görevler tartışılır."},
                {"Türkçede 'to hire' nasıl denir?", "İşe almak", "Satmak", "Gitmek", "Yazmak", "İşe almak", "Yeni bir çalışanı ekibe dahil etmektir."},
                {"Normalden fazla çalıştığınızda ne alırsınız?", "Fazla mesai", "Tatil", "Ceza", "Hediye", "Fazla mesai", "Fazla mesai (Mesai) ek ödeme gerektirir."}
        };
    }
    private void seedForWorkTurkish(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = TurkishWorkEasy();
        var md = TurkishWorkEasy();
        var h = TurkishWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }

    private void seedForWorkDutch(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = TurkishWorkEasy();
        var md = TurkishWorkEasy();
        var h = TurkishWorkEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }

    private void seedForWorkThai(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = ThaiFamilyEasy();
        var md = ThaiFamilyEasy();
        var h = ThaiFamilyEasy();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] EnglishEducationEasy() {
        return new String[][] {
                {"My School", "Mastering basic classroom vocabulary"},
                {"Who is the person that teaches students in a classroom?", "Teacher", "Doctor", "Pilot", "Chef", "Teacher", "A teacher helps students learn new things."},
                {"What do you use to write on a piece of paper?", "Pen", "Eraser", "Ruler", "Glue", "Pen", "You can use a pen or a pencil to write."},
                {"Which subject focuses on numbers and calculations?", "Math", "History", "Art", "Music", "Math", "Mathematics involves addition, subtraction, and more."},
                {"What is the time called when students eat and play?", "Recess / Break time", "Exam", "Homework", "Lecture", "Recess / Break time", "It's a short period for relaxation between classes."},
                {"Where do students go to borrow and read books?", "Library", "Gym", "Cafeteria", "Parking lot", "Library", "The library is a quiet place for study."},
                {"What do you wear to school to look the same as other students?", "Uniform", "Pajamas", "Swimsuit", "Raincoat", "Uniform", "Many schools require students to wear a uniform."},
                {"What is a short test used to check your knowledge?", "Quiz", "Graduation", "Holiday", "Summer camp", "Quiz", "A quiz is usually shorter and less formal than an exam."},
                {"Which object shows the days, weeks, and months of the school year?", "Calendar", "Dictionary", "Globe", "Calculator", "Calendar", "The school calendar shows important dates."},
                {"What do you call a person who is studying at a school?", "Student", "Principal", "Coach", "Janitor", "Student", "Students go to school to gain knowledge."},
                {"What do you use to carry your books and notebooks?", "Backpack", "Wallet", "Spoon", "Mirror", "Backpack", "A backpack makes it easy to carry school supplies."}
        };
    }public static String[][] EnglishEducationMedium() {
        return new String[][] {
                {"Higher Education", "Understanding exams and university life"},
                {"What is the money given to a student to help pay for their studies?", "Scholarship", "Tax", "Fine", "Rent", "Scholarship", "Scholarships are usually based on academic merit."},
                {"What do you call the main subject a student studies at university?", "Major", "Minor", "Hobby", "Elective", "Major", "For example, 'My major is Computer Science.'"},
                {"What is the ceremony where students receive their diplomas?", "Graduation", "Wedding", "Concert", "Exhibition", "Graduation", "It marks the successful completion of a course of study."},
                {"Which exam is often required for international students to study in English?", "IELTS / TOEFL", "Driving test", "Bar exam", "CPA", "IELTS / TOEFL", "These tests measure your English language proficiency."},
                {"What is a teacher at a college or university called?", "Professor", "Nanny", "Officer", "Mechanic", "Professor", "Professors often conduct research in addition to teaching."},
                {"What do you call the piece of work a student does at home?", "Homework / Assignment", "Housework", "Workout", "Network", "Homework / Assignment", "Assignments help reinforce what was learned in class."},
                {"What is the official document that records a student's grades?", "Transcript", "Passport", "Receipt", "Menu", "Transcript", "Universities look at your transcript during the admission process."},
                {"What do you call a student who has completed their first degree?", "Graduate", "Fresher", "Junior", "Sophomore", "Graduate", "A graduate is someone who has earned a degree."},
                {"Which word refers to the subjects taught in a school or college?", "Curriculum", "Campus", "Tuition", "Faculty", "Curriculum", "The curriculum is the set of courses provided by an institution."},
                {"What is the fee you pay to a university for teaching?", "Tuition", "Interest", "Deposit", "Insurance", "Tuition", "Tuition fees vary depending on the university and the country."}
        };
    }public static String[][] EnglishEducationHard() {
        return new String[][] {
                {"Academia & Research", "Advanced terms for education systems"},
                {"What is the long essay written by a candidate for a PhD degree?", "Dissertation / Thesis", "Blog post", "Journal", "Abstract", "Dissertation / Thesis", "It involves original research on a specific subject."},
                {"What is the term for stealing someone else's work or ideas?", "Plagiarism", "Citation", "Collaboration", "Publication", "Plagiarism", "Plagiarism is a serious academic offense."},
                {"What do you call the process of evaluating the quality of an institution?", "Accreditation", "Recruitment", "Enrollment", "Internship", "Accreditation", "Accredited schools meet certain quality standards."},
                {"Which term refers to learning that continues throughout a person's life?", "Lifelong learning", "Rote learning", "Distance learning", "Vocational training", "Lifelong learning", "It's the ongoing pursuit of knowledge for personal or professional reasons."},
                {"What is the highest academic degree awarded by a university?", "Doctorate (PhD)", "Master's", "Bachelor's", "Associate's", "Doctorate (PhD)", "It stands for Doctor of Philosophy."},
                {"What do you call a university that is part of the elite group in the US?", "Ivy League", "Oxford", "Red Brick", "State College", "Ivy League", "The Ivy League includes eight prestigious private universities."},
                {"What is a teacher's permanent job status at a university?", "Tenure", "Internship", "Freelance", "Apprenticeship", "Tenure", "Tenure protects academic freedom by preventing arbitrary dismissal."},
                {"What is the term for a short summary of a research paper?", "Abstract", "Index", "Appendix", "Footnote", "Abstract", "An abstract helps readers quickly understand the paper's purpose."},
                {"What do you call the study of the methods of teaching?", "Pedagogy", "Psychology", "Sociology", "Archeology", "Pedagogy", "Pedagogy focuses on the theory and practice of education."},
                {"Which type of learning involves students staying at the school?", "Boarding school", "Day school", "Homeschooling", "Sunday school", "Boarding school", "Students live in dormitories during the school term."}
        };
    }
    private void seedForEducationEnglish(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = EnglishEducationEasy();
        var md = EnglishEducationMedium();
        var h = EnglishEducationHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }
    public static String[][] VietnameseEducationEasy() {
        return new String[][] {
                {"Trường học của em", "Làm quen với các từ vựng cơ bản về giáo dục"},
                {"Người truyền dạy kiến thức cho học sinh trên lớp là ai?", "Thầy cô giáo", "Bác sĩ", "Kỹ sư", "Công nhân", "Thầy cô giáo", "Giáo viên là người hướng dẫn và dạy dỗ học sinh."},
                {"Vật dụng nào dùng để viết hoặc vẽ lên bảng đen?", "Phấn", "Thước kẻ", "Cặp sách", "Cục tẩy", "Phấn", "Ngày nay nhiều trường đã chuyển sang dùng bảng trắng và bút lông."},
                {"Bậc học đầu tiên sau khi trẻ rời trường mầm non là gì?", "Tiểu học", "Trung học", "Đại học", "Cao học", "Tiểu học", "Ở Việt Nam, tiểu học bao gồm từ lớp 1 đến lớp 5."},
                {"Vào giờ giải lao, học sinh thường làm gì?", "Ra sân chơi", "Đi ngủ ở nhà", "Đi làm thêm", "Đi thi", "Ra sân chơi", "Giờ ra chơi giúp học sinh thư giãn sau các tiết học căng thẳng."},
                {"Vật dụng nào giúp em đựng sách vở đến trường?", "Cặp sách / Balo", "Mũ bảo hiểm", "Túi ngủ", "Vali", "Cặp sách / Balo", "Một chiếc cặp sách nhẹ giúp bảo vệ cột sống của học sinh."},
                {"Kỳ nghỉ dài nhất trong năm học thường vào mùa nào?", "Mùa hè", "Mùa đông", "Mùa xuân", "Mùa thu", "Mùa hè", "Kỳ nghỉ hè thường kéo dài khoảng 2 đến 3 tháng."},
                {"Cuốn sách ghi chép các bài giảng trên lớp gọi là gì?", "Vở ghi", "Sách giáo khoa", "Từ điển", "Truyện tranh", "Vở ghi", "Việc ghi chép cẩn thận giúp học sinh ôn tập tốt hơn."},
                {"Học sinh mặc bộ quần áo giống nhau khi đến trường gọi là gì?", "Đồng phục", "Quần áo ngủ", "Đồ bơi", "Áo khoác", "Đồng phục", "Đồng phục tạo nên sự bình đẳng và bản sắc của trường."},
                {"Bảng thông báo kết quả học tập sau mỗi học kỳ gọi là gì?", "Sổ liên lạc / Học bạ", "Hóa đơn", "Thẻ thư viện", "Giấy khen", "Sổ liên lạc / Học bạ", "Đây là nơi ghi nhận điểm số và nhận xét của giáo viên."},
                {"Môn học nghiên cứu về các con số và hình học là gì?", "Toán học", "Ngữ văn", "Lịch sử", "Địa lý", "Toán học", "Toán học giúp rèn luyện tư duy logic."},
                {"Nơi chứa rất nhiều sách để học sinh mượn và đọc là gì?", "Thư viện", "Căng tin", "Phòng y tế", "Nhà xe", "Thư viện", "Thư viện là kho tàng kiến thức của mỗi ngôi trường."}
        };
    }public static String[][] VietnameseEducationMedium() {
        return new String[][] {
                {"Hệ thống giáo dục", "Tìm hiểu về thi cử và phương pháp học tập"},
                {"Kỳ thi quan trọng nhất sau 12 năm đèn sách ở Việt Nam là gì?", "Thi tốt nghiệp THPT Quốc gia", "Thi học kỳ", "Thi bằng lái xe", "Thi Olympic", "Thi tốt nghiệp THPT Quốc gia", "Kết quả kỳ thi này thường dùng để xét tuyển vào Đại học."},
                {"Học sinh đạt kết quả học tập xuất sắc thường nhận được gì?", "Học bổng / Giấy khen", "Hình phạt", "Thêm bài tập", "Thông báo nhắc nhở", "Học bổng / Giấy khen", "Đây là sự khích lệ cho những nỗ lực trong học tập."},
                {"Việc tự học tại nhà sau giờ lên lớp gọi là gì?", "Tự học / Làm bài tập về nhà", "Đi chơi", "Ngủ nướng", "Xem TV", "Tự học / Làm bài tập về nhà", "Tự học giúp học sinh nắm vững kiến thức hơn."},
                {"Người đứng đầu quản lý một trường học gọi là gì?", "Hiệu trưởng", "Trưởng phòng", "Giám đốc", "Chủ tịch", "Hiệu trưởng", "Hiệu trưởng chịu trách nhiệm về mọi hoạt động của nhà trường."},
                {"Chứng chỉ chứng minh khả năng sử dụng tiếng Anh phổ biến là gì?", "IELTS / TOEFL", "Bằng lái xe", "Thẻ căn cước", "Giấy khai sinh", "IELTS / TOEFL", "Các chứng chỉ này rất quan trọng khi đi du học hoặc xin việc."},
                {"Hoạt động ngoại khóa thường bao gồm những gì?", "Câu lạc bộ, thể thao, tình nguyện", "Chỉ ngồi học", "Đi ngủ", "Làm bài kiểm tra", "Câu lạc bộ, thể thao, tình nguyện", "Giúp phát triển kỹ năng mềm và thể chất."},
                {"Môn học dạy về quá khứ và các sự kiện trọng đại của đất nước?", "Lịch sử", "Vật lý", "Hóa học", "Sinh học", "Lịch sử", "Học lịch sử để hiểu rõ nguồn cội và tổ tiên."},
                {"Bậc học dành cho những người đã tốt nghiệp Đại học?", "Sau đại học (Thạc sĩ/Tiến sĩ)", "Trung học cơ sở", "Mầm non", "Tiểu học", "Sau đại học (Thạc sĩ/Tiến sĩ)", "Dành cho những người muốn nghiên cứu chuyên sâu."},
                {"Hình thức học qua mạng, không cần đến lớp gọi là gì?", "Học trực tuyến (Online)", "Học nội trú", "Học nghề", "Học thêm", "Học trực tuyến (Online)", "Hình thức này trở nên phổ biến sau đại dịch Covid-19."},
                {"Môi trường học tập mà học sinh ăn ở luôn tại trường gọi là gì?", "Trường nội trú", "Trường công lập", "Trường dân lập", "Trung tâm ngoại ngữ", "Trường nội trú", "Học sinh sẽ rèn luyện tính tự lập cao tại đây."}
        };
    }public static String[][] VietnameseEducationHard() {
        return new String[][] {
                {"Hàn lâm và Chính sách", "Các thuật ngữ giáo dục chuyên sâu"},
                {"Văn bản quy định nội dung dạy và học của một môn học gọi là gì?", "Chương trình đào tạo", "Sách hướng dẫn", "Nội quy", "Thời khóa biểu", "Chương trình đào tạo", "Đây là khung sườn cho toàn bộ quá trình giảng dạy."},
                {"Khả năng tự điều chỉnh và quản lý việc học của bản thân gọi là gì?", "Tư duy tự chủ", "Học vẹt", "Nghe lời", "Chép bài", "Tư duy tự chủ", "Đây là kỹ năng quan trọng nhất của người học suốt đời."},
                {"Việc đánh giá năng lực dựa trên chuẩn kiến thức kỹ năng gọi là gì?", "Kiểm định chất lượng", "Chấm điểm cảm tính", "Xếp hàng", "Bình bầu", "Kiểm định chất lượng", "Giúp đảm bảo công bằng và duy trì tiêu chuẩn giáo dục."},
                {"Thuật ngữ chỉ sự học tập không ngừng nghỉ suốt cuộc đời?", "Học tập suốt đời (Lifelong learning)", "Học cấp tốc", "Học tủ", "Học hộ", "Học tập suốt đời (Lifelong learning)", "Xã hội hiện đại đòi hỏi con người phải cập nhật kiến thức liên tục."},
                {"Tên gọi của bài luận văn cuối cùng để nhận bằng Tiến sĩ là gì?", "Luận án tiến sĩ", "Bài kiểm tra miệng", "Tiểu luận", "Báo cáo thực tập", "Luận án tiến sĩ", "Đòi hỏi sự đóng góp mới mẻ về mặt khoa học."},
                {"Phương pháp giáo dục tích hợp Khoa học, Công nghệ, Kỹ thuật và Toán học?", "STEM", "Montessori", "Traditional", "Homeschooling", "STEM", "STEM đang là xu hướng giáo dục toàn cầu để phát triển công nghệ."},
                {"Việc thừa nhận bằng cấp của một quốc gia tại quốc gia khác gọi là gì?", "Công nhận văn bằng tương đương", "Đổi bằng", "Dịch thuật", "Công chứng", "Công nhận văn bằng tương đương", "Rất quan trọng trong quá trình hội nhập và du học."},
                {"Trình độ cao nhất trong hệ thống giáo dục quốc dân là gì?", "Tiến sĩ", "Thạc sĩ", "Cử nhân", "Kỹ sư", "Tiến sĩ", "Người đạt trình độ này có khả năng nghiên cứu độc lập."},
                {"Thuật ngữ chỉ việc các trường đại học được tự quyết định về tài chính và nhân sự?", "Tự chủ đại học", "Quản lý tập trung", "Phân quyền xã hội", "Xã hội hóa", "Tự chủ đại học", "Giúp các trường linh hoạt và sáng tạo hơn trong phát triển."},
                {"Việc gian lận trong thi cử, sao chép ý tưởng của người khác gọi là gì?", "Đạo văn", "Tham khảo", "Sáng tạo", "Trích dẫn", "Đạo văn", "Đây là hành vi vi phạm đạo đức nghiêm trọng trong môi trường học thuật."}
        };
    }
    private void seedForEducationVietNam(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = VietnameseEducationEasy();
        var md = VietnameseEducationMedium();
        var h = VietnameseEducationHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }


    public static String[][] VietnameseFamilyEasy() {
        return new String[][] {
                {"Chuyện về những người thân yêu", "Làm quen với các danh xưng và quan hệ hàng ngày"},
                {"Bạn gọi cha của cha mình là gì?", "Ông nội", "Ông ngoại", "Bác", "Ông cố", "Ông nội", "Trong tiếng Việt, cha của cha được gọi là ông nội."},
                {"Bạn gọi mẹ của mẹ mình là gì?", "Bà nội", "Bà ngoại", "Cô", "Bà cố", "Bà ngoại", "Trong tiếng Việt, mẹ của mẹ được gọi là bà ngoại."},
                {"Anh trai của bố hoặc mẹ thường được gọi là gì?", "Chú", "Anh họ", "Bác", "Anh rể", "Bác", "Ở Việt Nam, anh trai của bố hoặc mẹ thường được gọi là bác để chỉ thứ bậc cao hơn."},
                {"Em trai của bố được gọi là gì?", "Bác", "Chú", "Cậu", "Dượng", "Chú", "Em trai của bố được gọi là chú."},
                {"Em gái hoặc chị gái của bố được gọi là gì?", "Cô", "Dì", "Mợ", "Thím", "Cô", "Chị hoặc em gái của bố được gọi là cô."},
                {"Anh trai hoặc em trai của mẹ được gọi là gì?", "Chú", "Bác", "Cậu", "Dượng", "Cậu", "Anh hoặc em trai của mẹ được gọi là cậu."},
                {"Em gái của mẹ được gọi là gì?", "Cô", "Dì", "Mợ", "Thím", "Dì", "Em gái của mẹ được gọi là dì."},
                {"Khi đi học về gặp người lớn, bạn nên nói gì cho lễ phép?", "Con mới về", "Chào", "Chào buổi chiều", "Con chào cả nhà ạ", "Con chào cả nhà ạ", "Chào hỏi kèm theo từ 'ạ' thể hiện sự kính trọng trong văn hóa Việt."},
                {"Con của cô, dì, chú, bác được gọi chung là gì?", "Anh em ruột", "Anh em họ", "Bạn bè", "Người lạ", "Anh em họ", "Con của anh chị em bố mẹ là anh em họ."},
                {"Vợ của chú được gọi là gì?", "Cô", "Dì", "Mợ", "Thím", "Thím", "Vợ của chú (em trai bố) được gọi là thím."},
                {"Vợ của cậu được gọi là gì?", "Cô", "Dì", "Mợ", "Thím", "Mợ", "Vợ của cậu (anh em trai của mẹ) được gọi là mợ."},
                {"Chồng của dì được gọi là gì?", "Chú", "Bác", "Dượng", "Cậu", "Dượng", "Chồng của dì hoặc cô thường được gọi là dượng."},
                {"Nhóm gồm bố mẹ và con cái sống cùng nhau gọi là gì?", "Gia đình tam đại", "Gia đình hạt nhân", "Đại gia đình", "Gia đình đơn thân", "Gia đình hạt nhân", "Gia đình hạt nhân gồm hai thế hệ: bố mẹ và con cái."},
                {"Từ nào dùng để chỉ người phụ nữ đã sinh ra bạn?", "Bà", "Mẹ", "Chị", "Cô", "Mẹ", "Mẹ là người mang nặng đẻ đau và nuôi nấng con cái."},
                {"Lễ mừng thọ thường dành cho đối tượng nào?", "Trẻ em", "Người cao tuổi", "Thanh niên", "Phụ nữ", "Người cao tuổi", "Mừng thọ là lễ mừng sức khỏe cho ông bà, cha mẹ khi về già."},
                {"Trẻ em nên làm gì trước khi ăn cơm để thể hiện sự lễ phép?", "Ăn trước", "Mời người lớn ăn cơm", "Vừa ăn vừa nói", "Cứ thế ăn", "Mời người lớn ăn cơm", "Mời cơm là nét văn hóa truyền thống đẹp của người Việt."},
                {"Anh trai của mẹ bạn gọi là gì?", "Chú", "Cậu", "Bác", "Dượng", "Bác", "Nhiều vùng miền gọi anh trai của mẹ là bác để thể hiện thứ bậc."},
                {"Con của anh chị em ruột gọi bạn là gì?", "Bố/Mẹ", "Ông/Bà", "Cô/Dì/Chú/Bác", "Bạn", "Cô/Dì/Chú/Bác", "Tùy vào thứ bậc của bạn với anh chị em mà các cháu sẽ gọi tương ứng."},
                {"Từ nào đồng nghĩa với từ 'Bố'?", "Ba", "Tía", "Thân phụ", "Tất cả các đáp án trên", "Tất cả các đáp án trên", "Bố, ba, tía, thân phụ đều là cách gọi người cha."},
                {"Ngày Quốc tế Thiếu nhi 1/6 dành cho ai?", "Ông bà", "Bố mẹ", "Trẻ em", "Người giúp việc", "Trẻ em", "Đây là ngày lễ dành riêng cho trẻ em trên toàn thế giới."}
        };
    }public static String[][] VietnameseFamilyMedium() {
        return new String[][] {
                {"Giữ gìn nếp nhà", "Tìm hiểu thành ngữ, tục ngữ và đạo đức kính trên nhường dưới"},
                {"Điền vào chỗ trống: 'Công cha như núi...'?", "Thái Sơn", "Trời xanh", "Mây cao", "Trường Sơn", "Thái Sơn", "Câu tục ngữ so sánh công lao to lớn của người cha với núi Thái Sơn."},
                {"Điền vào chỗ trống: 'Nghĩa mẹ như nước trong... chảy ra'?", "Suối", "Nguồn", "Sông", "Biển", "Nguồn", "Tình yêu của mẹ được ví như nguồn nước không bao giờ cạn."},
                {"Thành ngữ 'Mẹ tròn con vuông' dùng để chỉ điều gì?", "Hình dáng", "Sức khỏe tốt sau sinh", "Vẻ đẹp", "Sự ngoan ngoãn", "Sức khỏe tốt sau sinh", "Đây là lời chúc sản phụ sinh nở bình an, thuận lợi."},
                {"Câu 'Chị ngã em nâng' khuyên chúng ta điều gì?", "Cười nhạo", "Đùm bọc, giúp đỡ nhau", "Đứng nhìn", "Tránh xa", "Đùm bọc, giúp đỡ nhau", "Khuyên anh chị em trong nhà phải biết giúp đỡ lẫn nhau khi gặp khó khăn."},
                {"Cụm từ 'Con Rồng cháu Tiên' nói về nguồn gốc của dân tộc nào?", "Trung Quốc", "Việt Nam", "Lào", "Thái Lan", "Việt Nam", "Người Việt luôn tự hào mình là con cháu của Lạc Long Quân và Âu Cơ."},
                {"'Gia phong' có nghĩa là gì?", "Gió trong nhà", "Nền nếp, truyền thống gia đình", "Sự giàu có", "Nhà cửa rộng rãi", "Nền nếp, truyền thống gia đình", "Gia phong là những thói quen, quy tắc tốt đẹp được truyền lại trong gia đình."},
                {"Điền vào chỗ trống: 'Con có cha như nhà có...'?", "Cửa", "Nóc", "Cột", "Móng", "Nóc", "Người cha là trụ cột, che chở cho gia đình như mái nóc của ngôi nhà."},
                {"Điền vào chỗ trống: 'Anh em như... / Rách lành đùm bọc dở hay đỡ đần'?", "Tay chân", "Chân tay", "Bầu bí", "Môi răng", "Tay chân", "Anh em được ví như tay với chân, không thể tách rời trên một cơ thể."},
                {"Lễ 'Thượng thọ' thường được tổ chức khi người già đạt bao nhiêu tuổi?", "50 tuổi", "60 tuổi", "Từ 70 tuổi trở lên", "100 tuổi", "Từ 70 tuổi trở lên", "Thường từ 70 hoặc 80 tuổi trở lên thì được gọi là lễ Thượng thọ."},
                {"'Kính trên nhường...' là quy tắc giao tiếp cơ bản?", "Dưới", "Bạn", "Sau", "Em", "Dưới", "Nghĩa là tôn trọng người lớn tuổi và nhường nhịn người nhỏ tuổi hơn."},
                {"Câu thành ngữ nào nói về lòng biết ơn cha mẹ, tổ tiên?", "Ăn cháo đá bát", "Uống nước nhớ nguồn", "Môi hở răng lạnh", "Lá lành đùm lá rách", "Uống nước nhớ nguồn", "Sự biết ơn tổ tiên, cha mẹ giống như việc uống nước phải nhớ về nguồn cội."},
                {"Ai là người đại diện họ hàng phát biểu trong lễ cưới?", "Chú rể", "Người dẫn chương trình", "Đại diện gia đình", "Phù dâu", "Đại diện gia đình", "Thường là người lớn tuổi, có uy tín trong dòng họ."},
                {"'Giỗ đầu' là ngày kỷ niệm người thân mất được bao lâu?", "100 ngày", "1 năm", "2 năm", "3 năm", "1 năm", "Giỗ đầu là ngày kỷ niệm tròn một năm kể từ khi người thân qua đời."},
                {"Thành ngữ 'Con dại cái mang' có nghĩa là gì?", "Con làm sai bố mẹ chịu", "Mẹ mang đồ hộ con", "Con hư do mẹ", "Mẹ chiều con", "Con làm sai bố mẹ chịu", "Bố mẹ luôn là người gánh vác trách nhiệm và bao dung cho lỗi lầm của con."},
                {"Mục đích của lễ 'Đầy tháng' là gì?", "Mừng con 1 tuổi", "Tạ ơn các Bà Mụ", "Chào mừng thành viên mới", "Cả B và C", "Cả B và C", "Lễ này tạ ơn các vị thần đã nặn ra đứa trẻ và giới thiệu bé với họ hàng."},
                {"Điền tiếp: 'Cá không ăn muối cá ươn / Con cãi cha mẹ...'?", "Trăm đường con hư", "Chẳng tích sự gì", "Nhà không có nóc", "Khổ một đời", "Trăm đường con hư", "Lời khuyên con cái nên lắng nghe những điều hay lẽ phải của cha mẹ."},
                {"Ai thường được coi là người 'tay hòm chìa khóa' trong nhà?", "Người cha", "Người mẹ", "Con cả", "Ông bà", "Người mẹ", "Người mẹ thường là người quản lý tài chính và vun vén các việc trong gia đình."},
                {"'Tứ đại đồng đường' nghĩa là gì?", "4 thế hệ sống chung", "4 con đi học", "4 căn nhà gần nhau", "Nhóm 4 người", "4 thế hệ sống chung", "Đây là nếp nhà xưa khi cụ, ông bà, bố mẹ và con cháu cùng sống dưới một mái nhà."},
                {"Thuật ngữ 'Huyết thống' dùng để chỉ điều gì?", "Bạn thân", "Cùng dòng máu, tổ tiên", "Hàng xóm", "Đồng nghiệp", "Cùng dòng máu, tổ tiên", "Chỉ mối quan hệ giữa những người có chung tổ tiên, dòng máu."},
                {"Ngày 28/6 hằng năm là ngày gì ở Việt Nam?", "Ngày Phụ nữ", "Ngày Trẻ em", "Ngày Gia đình Việt Nam", "Ngày Người cao tuổi", "Ngày Gia đình Việt Nam", "Ngày tôn vinh những giá trị gia đình và sự gắn kết giữa các thành viên."}
        };
    }public static String[][] VietnameseFamilyHard() {
        return new String[][] {
                {"Tìm về cội nguồn", "Khám phá văn hóa dòng họ, các nghi lễ và từ Hán Việt chuyên sâu"},
                {"'Từ đường' là tên gọi khác của loại công trình nào?", "Nhà ở", "Nhà thờ họ", "Trường học", "Chùa", "Nhà thờ họ", "Nơi thờ cúng tổ tiên của một dòng tộc hoặc một chi họ."},
                {"Thuật ngữ 'Thúc bá' dùng để chỉ ai?", "Anh em trai của cha", "Anh em trai của mẹ", "Ông bà", "Con cháu", "Anh em trai của cha", "'Thúc' (chú) và 'Bá' (bác) là các anh em trai của người cha."},
                {"Trong quan hệ thân tộc, 'Nội tộc' chỉ phía nào?", "Bên mẹ", "Bên cha", "Bên vợ", "Bên chồng", "Bên cha", "Nội tộc là những người thuộc dòng họ bên cha."},
                {"'Ngũ đại mai thần' có nghĩa là gì?", "5 thế hệ sống cùng nhau", "5 người con tài giỏi", "5 loại cây trong vườn", "5 hướng nhà", "5 thế hệ sống cùng nhau", "Đây là thuật ngữ chỉ gia đình cực kỳ có phúc khi có 5 thế hệ cùng chung sống."},
                {"Lễ 'Cúng tất niên' thường được tổ chức vào khi nào?", "Sáng mồng 1 Tết", "Chiều cuối năm âm lịch", "Rằm tháng Giêng", "Ngày ông Công ông Táo", "Chiều cuối năm âm lịch", "Bữa cơm sum họp để báo cáo với tổ tiên về một năm đã qua."},
                {"'Thân phụ' và 'Thân mẫu' là cách gọi trang trọng của?", "Cha và Mẹ", "Ông và Bà", "Anh và Chị", "Chú và Cô", "Cha và Mẹ", "Từ Hán Việt dùng trong văn bản chính quy hoặc cách nói tôn kính."},
                {"Con của chị/em gái của bố được gọi là gì đối với bạn?", "Anh em họ nội", "Anh em họ ngoại", "Cô dì", "Họ hàng xa", "Anh em họ ngoại", "Theo quan niệm xưa, con của cô thường được coi là họ ngoại (ngoại tộc)."},
                {"'Gia phả' là gì?", "Sách nấu ăn", "Sổ ghi chép lịch sử dòng họ", "Quy tắc trong nhà", "Danh sách thành viên", "Sổ ghi chép lịch sử dòng họ", "Cuốn sổ ghi chép nguồn gốc, danh tính các đời trong một dòng họ."},
                {"Bậc cao nhất trong 'Cửu tộc' tính từ mình lên là ai?", "Ông nội", "Cụ nội", "Cao tổ (Cụ cố đời thứ 4)", "Thủy tổ", "Cao tổ (Cụ cố đời thứ 4)", "Cửu tộc gồm 9 đời từ Cao, Tằng, Tổ, Khảo, Kỷ, Tử, Tôn, Tằng, Huyền."},
                {"'Hôn nhân đại sự' có nghĩa là gì?", "Cưới hỏi là việc lớn", "Đám cưới to", "Lấy vợ vì tiền", "Hủy bỏ đám cưới", "Cưới hỏi là việc lớn", "Quan niệm xưa coi việc cưới hỏi là việc quan trọng nhất đời người."},
                {"'Ngoại tộc' dùng để chỉ những người thuộc bên nào?", "Bên cha", "Bên mẹ", "Hàng xóm", "Bạn bè", "Bên mẹ", "Ngoại tộc dùng để chỉ dòng họ thuộc bên người mẹ."},
                {"'Môn đăng hộ đối' trong hôn nhân xưa nghĩa là gì?", "Nhà ở gần nhau", "Địa vị, kinh tế tương đương", "Bằng tuổi nhau", "Cùng sở thích", "Địa vị, kinh tế tương đương", "Quan niệm chọn đối tượng kết hôn có hoàn cảnh gia đình tương đồng."},
                {"'Trưởng tộc' là người như thế nào?", "Người giàu nhất", "Người đứng đầu dòng họ", "Người già nhất làng", "Người trẻ nhất", "Người đứng đầu dòng họ", "Người chịu trách nhiệm thờ cúng và quyết định việc lớn của dòng họ."},
                {"Lễ 'Thanh minh' gắn liền với hoạt động gia đình nào?", "Gói bánh chưng", "Tả mộ (thăm và dọn mộ)", "Đi du lịch", "Mở tiệc", "Tả mộ (thăm và dọn mộ)", "Thời điểm con cháu về thăm nom, sửa sang phần mộ tổ tiên."},
                {"Lời chúc 'Bách niên giai lão' thường dành cho ai?", "Trẻ em", "Cặp đôi mới cưới", "Người già", "Người đi làm xa", "Cặp đôi mới cưới", "Chúc cho vợ chồng sống hạnh phúc bên nhau đến trăm tuổi."},
                {"Trong đám tang xưa, người chống gậy, đội mũ rơm là ai?", "Con trai ruột", "Con gái", "Cháu nội", "Người lạ", "Con trai ruột", "Tục lệ xưa con trai phải chống gậy để thể hiện sự đau xót và lòng hiếu thảo."},
                {"'Phụ từ tử hiếu' có nghĩa là gì?", "Cha hiền con hiếu", "Cha nghiêm con sợ", "Cha con bằng vai", "Con thay cha làm việc", "Cha hiền con hiếu", "Nguyên tắc đạo đức: Cha mẹ thương yêu, con cái hiếu thảo."},
                {"'Tam đại đồng đường' chỉ gia đình có mấy thế hệ?", "2 thế hệ", "3 thế hệ", "4 thế hệ", "5 thế hệ", "3 thế hệ", "Gồm ông bà, bố mẹ và con cái sống chung dưới một mái nhà."},
                {"Lễ 'Dạm ngõ' là giai đoạn nào trong cưới hỏi?", "Đám cưới chính thức", "Gặp mặt chính thức đầu tiên", "Lễ rước dâu", "Lễ lại mặt", "Gặp mặt chính thức đầu tiên", "Nghi lễ đơn giản để hai nhà chính thức thưa chuyện cho đôi trẻ tìm hiểu nhau."}
        };
    }

    private void seedLastOnline() {
        for (int i = 0; i < 21; i++) {
            String email = i == 4 ? "ChatBot@gmail.com" : "fakeUser" + i + "@gmail.com";
            Long lastOnlineSecondFromEpoch = this.redisService.getKey(this.redisService.getKeyLastOnlineWithPrefix(email), Long.class);
            if (lastOnlineSecondFromEpoch == null) {
                this.redisService.setKey(this.redisService.getKeyLastOnlineWithPrefix(email), Instant.now().getEpochSecond());
            }
        }
    }

    private void seedAttachment() throws IOException {
        if (attachmentRepository.count() == 0) {
            // Inline code - chỉ cần thay "sample.jpg" thành tên ảnh của bạn
            // Upload file
            for (int i = 0; i < 21; i++) {
                ClassPathResource resource = new ClassPathResource("image/abc.png");
                byte[] imageBytes = resource.getInputStream().readAllBytes();
                MultipartFile multipartFile = new MockMultipartFile("image", "abc.png", "image/png", imageBytes);
                String originalFileName = multipartFile.getOriginalFilename();
                String contentType = multipartFile.getContentType();
                Long fileSize = multipartFile.getSize();

                TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
                String fileNameInS3 = generator.generate().toString() + "_" + originalFileName;

                log.info("fileNameInS3: {}", fileNameInS3);
                log.info("contentType: {}", contentType);
                log.info("fileSize: {}", fileSize);

                // Upload trực tiếp lên S3
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(contentType);
                metadata.setContentLength(fileSize);

                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        S3Constant.AVATAR_PRIVATE_BUCKET,
                        fileNameInS3,
                        multipartFile.getInputStream(),
                        metadata
                );
                PutObjectResult result = s3Client.putObject(putObjectRequest);
                log.info("Upload successful. ETag: {}", result.getETag());

                Attachment attachmentToSave = Attachment.builder()
                        .originalFileName(originalFileName)
                        .s3BucketName(S3Constant.AVATAR_PRIVATE_BUCKET)
                        .fileNameInS3(fileNameInS3)
                        .fileSize(fileSize)
                        .contentType(contentType)
                        .status(Attachment.STATUS.CONFIRMED) // Đổi thành SUCCESS vì đã upload xong
                        .expireAt(LocalDateTime.now().plusDays(S3Constant.EXPIRE_TIME_ATTACHMENT))
                        .createdBy(1L)
                        .build();

                Attachment savedAttachment = this.attachmentRepository.save(attachmentToSave);
            }

        }
    }

    private void seedTopic() throws Exception {
        try {
            if (topicTestRepository.count() == 0) {
                var topic1 = TopicTest.builder()
                        .name("Family")
                        .build();
                var topic2 = TopicTest.builder()
                        .name("Work")
                        .build();
                var topic3 = TopicTest.builder()
                        .name("Education")
                        .build();
//                var topic4 = TopicTest.builder()
//                        .name("Music")
//                        .build();
//                var topic5 = TopicTest.builder()
//                        .name("Entertainment")
//                        .build();
                topicTestRepository.saveAll(List.of(topic1, topic2, topic3));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedDifficulty() throws Exception {
        try {
            if (difficultyTestRepository.count() == 0) {
                var diff1 = DifficultyTests.builder()
                        .name("Easy")
                        .build();
                var diff2 = DifficultyTests.builder()
                        .name("Medium")
                        .build();
                var diff3 = DifficultyTests.builder()
                        .name("Hard")
                        .build();
                difficultyTestRepository.save(diff1);
                difficultyTestRepository.save(diff2);
                difficultyTestRepository.save(diff3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedForFamilyVietNam(TopicTest topicTest, LearningLanguage learningLanguage) {
        var es = VietnameseFamilyEasy();
        var md = VietnameseFamilyMedium();
        var h = VietnameseFamilyHard();

        tmp(topicTest, learningLanguage, es, md, h);
    }

    public void tmp(TopicTest topicTest, LearningLanguage learningLanguage, String[][] es, String[][] md, String[][] h) {
        var easy = difficultyTestRepository.findById(1L).get();
        var medium = difficultyTestRepository.findById(2L).get();
        var hard = difficultyTestRepository.findById(3L).get();

        var test1 = Test.builder()
                .title(es[0][0])
                .subTitle(es[0][1])
                .difficultyTests(easy)
                .topicTest(topicTest)
                .learningLanguage(learningLanguage)
                .build();
        var test2 = Test.builder()
                .title(md[0][0])
                .subTitle(md[0][1])
                .difficultyTests(medium)
                .topicTest(topicTest)
                .learningLanguage(learningLanguage)
                .build();

        var test3 = Test.builder()
                .title(h[0][0])
                .subTitle(h[0][1])
                .difficultyTests(hard)
                .topicTest(topicTest)
                .learningLanguage(learningLanguage)
                .build();
        testRepository.saveAll(List.of(test1, test2, test3));

        seedForTest(test1, es);
        seedForTest(test2, md);
        seedForTest(test3, h);
    }

    public void seedForTest(Test test, String[][] questions) {
        for (int i = 1; i < questions.length; i++) {
            var question = questions[i];
            var contentQuestion = question[0];
            var contentOpt1 = question[1];
            var contentOpt2 = question[2];
            var contentOpt3 = question[3];
            var contentOpt4 = question[4];
            var contentAnswer = question[5];
            var explain = question[6];
            var quest = QuestionTests.builder()
                    .orderNumber((i + 1) * 1L)
                    .content(contentQuestion)
                    .test(test)
                    .explain(explain)
                    .build();
            questionTestRepository.saveAll(List.of(quest));


            var op1 = QuestionOptions.builder()
                    .content(contentOpt1)
                    .questionTest(quest)
                    .isAnswer(contentOpt1.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op2 = QuestionOptions.builder()
                    .content(contentOpt2)
                    .questionTest(quest)
                    .isAnswer(contentOpt2.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op3 = QuestionOptions.builder()
                    .content(contentOpt3)
                    .questionTest(quest)
                    .isAnswer(contentOpt3.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op4 = QuestionOptions.builder()
                    .content(contentOpt4)
                    .questionTest(quest)
                    .isAnswer(contentOpt4.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            questionOptionRepository.saveAll(List.of(op1, op2, op3, op4));
        }

    }

    private void seedUser() throws Exception {
        try {
            if (userRepository.count() == 0) {
                List<Attachment> attachments = this.attachmentRepository.findAll();
                List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAll();
                List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAll();

                for (int i = 0; i < 21; i++) {
                    Random random = new Random();
                    LearningLanguage learningLanguage = learningLanguages.get(random.nextInt(learningLanguages.size()));
                    NativeLanguage nativeLanguage = nativeLanguages.get(random.nextInt(nativeLanguages.size()));

                    Profile profile = this.profileRepository.save(Profile.builder()
                            .bio("XinchaoChatApp")
                            .location("VN")
                            .avatar(attachments.get(i))
                            .learningLanguage(learningLanguage)
                            .nativeLanguage(nativeLanguage)
                            .build());

                    User user = User.builder()
                            .email(i == 4 ? "ChatBot@gmail.com" : genEmailByIndex(i))
                            .password(passwordEncoder.encode("ChatApp123456@"))
                            .fullName(i == 4 ? "CHAT BOT" : genFullNameByIndex(i))
                            .status(User.STATUS.ACTIVE)
                            .role(User.ROLE.USER)
                            .profile(profile)
                            .build();


                    // Lưu cả 3 bản ghi vào database
                    userRepository.save(user);

                    LimitResource limitResource = LimitResource.builder()
                            .maxLimit(S3Constant.MAX_LIMIT_RESOURCE)
                            .type(LimitResource.TYPE.MEDIA)
                            .currentUsage(0L)
                            .userId(user.getId())
                            .build();
                    this.limitResourceRepository.save(limitResource);

                    GetProfileResponseDto userFullInfo = this.getFullInfoAboutUserService.call(user);
                    SynchronizeUserEvent synchronizeUserEvent = SynchronizeUserEvent.builder()
                            .userId(userFullInfo.getId())
                            .email(userFullInfo.getEmail())
                            .fullName(userFullInfo.getFullName())
                            .bio(userFullInfo.getBio())
                            .location(userFullInfo.getLocation())
                            .learningLanguageId(userFullInfo.getLearningLanguage().getId())
                            .nativeLanguageId(userFullInfo.getNativeLanguage().getId())
                            .nativeLanguageName(userFullInfo.getNativeLanguage().getName())
                            .learningLanguageName(userFullInfo.getLearningLanguage().getName())
                            .avatar(userFullInfo.getFileNameInS3())
                            .bucket(S3Constant.AVATAR_PRIVATE_BUCKET)
                            .status(User.STATUS.ACTIVE.toString())
                            .role(User.ROLE.USER.toString())
                            .maxLimitResourceMedia(S3Constant.MAX_LIMIT_RESOURCE)
                            .currentUsageResourceMedia(0L)
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .rowVersion(userFullInfo.getRowVersion())
                            .eventType(Event.TYPE.SYNC_USER)
                            .deleted(0)
                            .build();

                    String body = null;
                    try {
                        body = objectMapper.writeValueAsString(synchronizeUserEvent);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    if (i == 4) {
                        userRepository.delete(user);
                    }
                    log.info("synchronizeUserEvent: {}", body);
                    eventRepository.save(Event.builder()
                            .destination(syncStream)
                            .payload(body)
                            .status(Event.STATUS.WAIT_TO_SEND)
                            .build());
                }
            } else {
                log.info("📋 Database đã có dữ liệu User, bỏ qua việc seed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            userRepository.deleteAll();
        }
    }

    public String genEmailByIndex(int i) {
        String[] emails = {
                "john.doe@gmail.com",
                "jane.smith@gmail.com",
                "mike.johnson@gmail.com",
                "sarah.wilson@gmail.com",
                "david.brown@gmail.com",
                "emma.davis@gmail.com",
                "chris.miller@gmail.com",
                "lisa.garcia@gmail.com",
                "ryan.martinez@gmail.com",
                "ashley.lopez@gmail.com",
                "kevin.anderson@gmail.com",
                "amanda.taylor@gmail.com",
                "brandon.thomas@gmail.com",
                "jessica.white@gmail.com",
                "tyler.harris@gmail.com",
                "nicole.clark@gmail.com",
                "austin.lewis@gmail.com",
                "stephanie.walker@gmail.com",
                "jordan.hall@gmail.com",
                "melissa.young@gmail.com",
                "smith.young@gmail.com",
        };

        if (i >= 0 && i < emails.length) {
            return emails[i];
        }
        return "unknown@gmail.com";
    }

    public String genFullNameByIndex(int i) {
        String[] fullNames = {
                "John Doe",
                "Jane Smith",
                "Mike Johnson",
                "Sarah Wilson",
                "David Brown",
                "Emma Davis",
                "Chris Miller",
                "Lisa Garcia",
                "Ryan Martinez",
                "Ashley Lopez",
                "Kevin Anderson",
                "Amanda Taylor",
                "Brandon Thomas",
                "Jessica White",
                "Tyler Harris",
                "Nicole Clark",
                "Austin Lewis",
                "Stephanie Walker",
                "Jordan Hall",
                "Melissa Young",
                "Melissa Smith",
                "Bob Young"
        };

        if (i >= 0 && i < fullNames.length) {
            return fullNames[i];
        }
        return "Unknown User";
    }

    private void seedLearningNative() {
        if (learningLanguageRepository.count() == 0) {

            //--------------------------------------
            // Tạo đối tượng LearningLanguage cho mỗi mã ngôn ngữ
            LearningLanguage learningLanguageVN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.VN)
                    .build();
            LearningLanguage learningLanguageEN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.EN)
                    .build();
            LearningLanguage learningLanguageES = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.ES)
                    .build();
            LearningLanguage learningLanguageFR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.FR)
                    .build();
            LearningLanguage learningLanguageDE = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.DE)
                    .build();
            LearningLanguage learningLanguageCN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.CN)
                    .build();
            LearningLanguage learningLanguageJP = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.JP)
                    .build();
            LearningLanguage learningLanguageKR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.KR)
                    .build();
            LearningLanguage learningLanguageIN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.IN)
                    .build();
            LearningLanguage learningLanguageRU = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.RU)
                    .build();
            LearningLanguage learningLanguagePT = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.PT)
                    .build();
            LearningLanguage learningLanguageSA = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.SA)
                    .build();
            LearningLanguage learningLanguageIT = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.IT)
                    .build();
            LearningLanguage learningLanguageTR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.TR)
                    .build();
            LearningLanguage learningLanguageNL = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.NL)
                    .build();
            LearningLanguage learningLanguageTH = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.TH)
                    .build();

            // Tạo đối tượng NativeLanguage cho mỗi mã ngôn ngữ
            NativeLanguage nativeLanguageVN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.VN)
                    .build();
            NativeLanguage nativeLanguageEN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.EN)
                    .build();
            NativeLanguage nativeLanguageES = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.ES)
                    .build();
            NativeLanguage nativeLanguageFR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.FR)
                    .build();
            NativeLanguage nativeLanguageDE = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.DE)
                    .build();
            NativeLanguage nativeLanguageCN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.CN)
                    .build();
            NativeLanguage nativeLanguageJP = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.JP)
                    .build();
            NativeLanguage nativeLanguageKR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.KR)
                    .build();
            NativeLanguage nativeLanguageIN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.IN)
                    .build();
            NativeLanguage nativeLanguageRU = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.RU)
                    .build();
            NativeLanguage nativeLanguagePT = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.PT)
                    .build();
            NativeLanguage nativeLanguageSA = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.SA)
                    .build();
            NativeLanguage nativeLanguageIT = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.IT)
                    .build();
            NativeLanguage nativeLanguageTR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.TR)
                    .build();
            NativeLanguage nativeLanguageNL = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.NL)
                    .build();
            NativeLanguage nativeLanguageTH = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.TH)
                    .build();

            // Lưu tất cả LearningLanguage bằng vòng lặp
            List<LearningLanguage> learningLanguages = new ArrayList<>();
            learningLanguages.add(learningLanguageVN);
            learningLanguages.add(learningLanguageEN);
            learningLanguages.add(learningLanguageES);
            learningLanguages.add(learningLanguageFR);
            learningLanguages.add(learningLanguageDE);
            learningLanguages.add(learningLanguageCN);
            learningLanguages.add(learningLanguageJP);
            learningLanguages.add(learningLanguageKR);
            learningLanguages.add(learningLanguageIN);
            learningLanguages.add(learningLanguageRU);
            learningLanguages.add(learningLanguagePT);
            learningLanguages.add(learningLanguageSA);
            learningLanguages.add(learningLanguageIT);
            learningLanguages.add(learningLanguageTR);
            learningLanguages.add(learningLanguageNL);
            learningLanguages.add(learningLanguageTH);

            for (LearningLanguage language : learningLanguages) {
                learningLanguageRepository.save(language);
            }

// Lưu tất cả NativeLanguage bằng vòng lặp
            List<NativeLanguage> nativeLanguages = new ArrayList<>();
            nativeLanguages.add(nativeLanguageVN);
            nativeLanguages.add(nativeLanguageEN);
            nativeLanguages.add(nativeLanguageES);
            nativeLanguages.add(nativeLanguageFR);
            nativeLanguages.add(nativeLanguageDE);
            nativeLanguages.add(nativeLanguageCN);
            nativeLanguages.add(nativeLanguageJP);
            nativeLanguages.add(nativeLanguageKR);
            nativeLanguages.add(nativeLanguageIN);
            nativeLanguages.add(nativeLanguageRU);
            nativeLanguages.add(nativeLanguagePT);
            nativeLanguages.add(nativeLanguageSA);
            nativeLanguages.add(nativeLanguageIT);
            nativeLanguages.add(nativeLanguageTR);
            nativeLanguages.add(nativeLanguageNL);
            nativeLanguages.add(nativeLanguageTH);

            for (NativeLanguage language : nativeLanguages) {
                nativeLanguageRepository.save(language);
            }

            // Danh sách các LearningLanguageLocale cho tiếng Anh và tiếng Việt
            List<LearningLanguageLocale> learningLanguageLocales = new ArrayList<>();

            // Tiếng Việt
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Vietnamese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Tiếng Việt")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Anh
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("English")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("Tiếng Anh")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Tây Ban Nha
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageES)
                    .name("Spanish")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageES)
                    .name("Tiếng Tây Ban Nha")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Pháp
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageFR)
                    .name("French")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageFR)
                    .name("Tiếng Pháp")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Đức
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageDE)
                    .name("German")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageDE)
                    .name("Tiếng Đức")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Trung Quốc
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageCN)
                    .name("Mandarin")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageCN)
                    .name("Tiếng Quan Thoại")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nhật
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageJP)
                    .name("Japanese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageJP)
                    .name("Tiếng Nhật")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hàn
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageKR)
                    .name("Korean")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageKR)
                    .name("Tiếng Hàn")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hindi
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIN)
                    .name("Hindi")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIN)
                    .name("Tiếng Hindi")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nga
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageRU)
                    .name("Russian")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageRU)
                    .name("Tiếng Nga")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Bồ Đào Nha
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguagePT)
                    .name("Portuguese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguagePT)
                    .name("Tiếng Bồ Đào Nha")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ả Rập
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageSA)
                    .name("Arabic")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageSA)
                    .name("Tiếng Ả Rập")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ý
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIT)
                    .name("Italian")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIT)
                    .name("Tiếng Ý")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thổ Nhĩ Kỳ
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTR)
                    .name("Turkish")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTR)
                    .name("Tiếng Thổ Nhĩ Kỳ")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hà Lan
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageNL)
                    .name("Dutch")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageNL)
                    .name("Tiếng Hà Lan")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thái
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTH)
                    .name("Thai")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTH)
                    .name("Tiếng Thái")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            this.learningLanguageLocaleRepository.saveAll(learningLanguageLocales);

            // Danh sách các NativeLanguageLocale cho tiếng Anh và tiếng Việt
            List<NativeLanguageLocale> nativeLanguageLocales = new ArrayList<>();

            // Tiếng Việt
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Vietnamese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Tiếng Việt")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Anh
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("English")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("Tiếng Anh")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Tây Ban Nha
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageES)
                    .name("Spanish")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageES)
                    .name("Tiếng Tây Ban Nha")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Pháp
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageFR)
                    .name("French")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageFR)
                    .name("Tiếng Pháp")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Đức
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageDE)
                    .name("German")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageDE)
                    .name("Tiếng Đức")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Trung Quốc
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageCN)
                    .name("Mandarin")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageCN)
                    .name("Tiếng Quan Thoại")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nhật
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageJP)
                    .name("Japanese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageJP)
                    .name("Tiếng Nhật")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hàn
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageKR)
                    .name("Korean")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageKR)
                    .name("Tiếng Hàn")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hindi
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIN)
                    .name("Hindi")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIN)
                    .name("Tiếng Hindi")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nga
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageRU)
                    .name("Russian")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageRU)
                    .name("Tiếng Nga")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Bồ Đào Nha
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguagePT)
                    .name("Portuguese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguagePT)
                    .name("Tiếng Bồ Đào Nha")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ả Rập
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageSA)
                    .name("Arabic")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageSA)
                    .name("Tiếng Ả Rập")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ý
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIT)
                    .name("Italian")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIT)
                    .name("Tiếng Ý")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thổ Nhĩ Kỳ
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTR)
                    .name("Turkish")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTR)
                    .name("Tiếng Thổ Nhĩ Kỳ")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hà Lan
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageNL)
                    .name("Dutch")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageNL)
                    .name("Tiếng Hà Lan")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thái
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTH)
                    .name("Thai")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTH)
                    .name("Tiếng Thái")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            this.nativeLanguageLocaleRepository.saveAll(nativeLanguageLocales);

            log.info("✅ Seeded Learning Language Record:");
        } else {
            log.info("📋 Database đã có dữ liệu Learning Language, bỏ qua việc seed");
        }
    }

}
