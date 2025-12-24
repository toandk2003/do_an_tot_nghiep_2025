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
            testRepository.deleteAll();
            questionTestRepository.deleteAll();
            questionOptionRepository.deleteAll();
            this.seedTest();
        };
    }

    public void seedTest() {

       try {
           var English = this.learningLanguageRepository.findById(1L).get();
           var VietNam = this.learningLanguageRepository.findById(2L).get();
           var Spanish = this.learningLanguageRepository.findById(3L).get();
           var French = this.learningLanguageRepository.findById(4L).get();
           var German = this.learningLanguageRepository.findById(5L).get();
           var Chinese = this.learningLanguageRepository.findById(6L).get();
           var Japanese = this.learningLanguageRepository.findById(7L).get();
           var Korean = this.learningLanguageRepository.findById(8L).get();
           var Hindi = this.learningLanguageRepository.findById(9L).get();
           var Russian = this.learningLanguageRepository.findById(10L).get();
           var Portuguese = this.learningLanguageRepository.findById(11L).get();
           var Arabic = this.learningLanguageRepository.findById(12L).get();
           var Italian = this.learningLanguageRepository.findById(13L).get();
           var Turkish = this.learningLanguageRepository.findById(14L).get();
           var Dutch = this.learningLanguageRepository.findById(15L).get();
           var Thai = this.learningLanguageRepository.findById(16L).get();

           var topicFamily = topicTestRepository.findById(1L).get();//Family
           var topicWork = topicTestRepository.findById(2L).get(); //Work
           var topicEducation = topicTestRepository.findById(3L).get(); //Education
//           var topicMusic = topicTestRepository.findById(4L).get(); //Music
//           var topicEntertainment = topicTestRepository.findById(5L).get(); //Entertainment


           seedTestFor(English, topicFamily, getFamilyQuestionsByLanguage(1L));
           seedTestFor(English, topicWork, getWorkQuestionsByLanguage(1L));
           seedTestFor(English, topicEducation, getEduQuestionsByLanguage(1L));
//           seedTestFor(English, topicMusic, getMusicQuestionsByLanguage(1L));
//           seedTestFor(English, topicEntertainment, getEnterQuestionsByLanguage(1L));

           seedTestFor(VietNam, topicFamily, getFamilyQuestionsByLanguage(2L));
           seedTestFor(VietNam, topicWork, getWorkQuestionsByLanguage(2L));
           seedTestFor(VietNam, topicEducation, getEduQuestionsByLanguage(2L));
//           seedTestFor(VietNam, topicMusic, getMusicQuestionsByLanguage(2L));
//           seedTestFor(VietNam, topicEntertainment, getEnterQuestionsByLanguage(2L));

           seedTestFor(Spanish, topicFamily, getFamilyQuestionsByLanguage(3L));
           seedTestFor(Spanish, topicWork, getWorkQuestionsByLanguage(3L));
           seedTestFor(Spanish, topicEducation, getEduQuestionsByLanguage(3L));
//           seedTestFor(Spanish, topicMusic, getMusicQuestionsByLanguage(3L));
//           seedTestFor(Spanish, topicEntertainment, getEnterQuestionsByLanguage(3L));

           seedTestFor(French, topicFamily, getFamilyQuestionsByLanguage(4L));
           seedTestFor(French, topicWork, getWorkQuestionsByLanguage(4L));
           seedTestFor(French, topicEducation, getEduQuestionsByLanguage(4L));
//           seedTestFor(French, topicMusic, getMusicQuestionsByLanguage(4L));
//           seedTestFor(French, topicEntertainment, getEnterQuestionsByLanguage(4L));

           seedTestFor(German, topicFamily, getFamilyQuestionsByLanguage(5L));
           seedTestFor(German, topicWork, getWorkQuestionsByLanguage(5L));
           seedTestFor(German, topicEducation, getEduQuestionsByLanguage(5L));
//           seedTestFor(German, topicMusic, getMusicQuestionsByLanguage(5L));
//           seedTestFor(German, topicEntertainment, getEnterQuestionsByLanguage(5L));

           seedTestFor(Chinese, topicFamily, getFamilyQuestionsByLanguage(6L));
           seedTestFor(Chinese, topicWork, getWorkQuestionsByLanguage(6L));
           seedTestFor(Chinese, topicEducation, getEduQuestionsByLanguage(6L));
//           seedTestFor(Chinese, topicMusic, getMusicQuestionsByLanguage(6L));
//           seedTestFor(Chinese, topicEntertainment, getEnterQuestionsByLanguage(6L));

           seedTestFor(Japanese, topicFamily, getFamilyQuestionsByLanguage(7L));
           seedTestFor(Japanese, topicWork, getWorkQuestionsByLanguage(7L));
           seedTestFor(Japanese, topicEducation, getEduQuestionsByLanguage(7L));
//           seedTestFor(Japanese, topicMusic, getMusicQuestionsByLanguage(7L));
//           seedTestFor(Japanese, topicEntertainment, getEnterQuestionsByLanguage(7L));

           seedTestFor(Korean, topicFamily, getFamilyQuestionsByLanguage(8L));
           seedTestFor(Korean, topicWork, getWorkQuestionsByLanguage(8L));
           seedTestFor(Korean, topicEducation, getEduQuestionsByLanguage(8L));
//           seedTestFor(Korean, topicMusic, getMusicQuestionsByLanguage(8L));
//           seedTestFor(Korean, topicEntertainment, getEnterQuestionsByLanguage(8L));

           seedTestFor(Hindi, topicFamily, getFamilyQuestionsByLanguage(9L));
           seedTestFor(Hindi, topicWork, getWorkQuestionsByLanguage(9L));
           seedTestFor(Hindi, topicEducation, getEduQuestionsByLanguage(9L));
//           seedTestFor(Hindi, topicMusic, getMusicQuestionsByLanguage(9L));
//           seedTestFor(Hindi, topicEntertainment, getEnterQuestionsByLanguage(9L));

           seedTestFor(Russian, topicFamily, getFamilyQuestionsByLanguage(10L));
           seedTestFor(Russian, topicWork, getWorkQuestionsByLanguage(10L));
           seedTestFor(Russian, topicEducation, getEduQuestionsByLanguage(10L));
//           seedTestFor(Russian, topicMusic, getMusicQuestionsByLanguage(10L));
//           seedTestFor(Russian, topicEntertainment, getEnterQuestionsByLanguage(10L));

           seedTestFor(Portuguese, topicFamily, getFamilyQuestionsByLanguage(11L));
           seedTestFor(Portuguese, topicWork, getWorkQuestionsByLanguage(11L));
           seedTestFor(Portuguese, topicEducation, getEduQuestionsByLanguage(11L));
//           seedTestFor(Portuguese, topicMusic, getMusicQuestionsByLanguage(11L));
//           seedTestFor(Portuguese, topicEntertainment, getEnterQuestionsByLanguage(11L));

           seedTestFor(Arabic, topicFamily, getFamilyQuestionsByLanguage(12L));
           seedTestFor(Arabic, topicWork, getWorkQuestionsByLanguage(12L));
           seedTestFor(Arabic, topicEducation, getEduQuestionsByLanguage(12L));
//           seedTestFor(Arabic, topicMusic, getMusicQuestionsByLanguage(12L));
//           seedTestFor(Arabic, topicEntertainment, getEnterQuestionsByLanguage(12L));

           seedTestFor(Italian, topicFamily, getFamilyQuestionsByLanguage(13L));
           seedTestFor(Italian, topicWork, getWorkQuestionsByLanguage(13L));
           seedTestFor(Italian, topicEducation, getEduQuestionsByLanguage(13L));
//           seedTestFor(Italian, topicMusic, getMusicQuestionsByLanguage(13L));
//           seedTestFor(Italian, topicEntertainment, getEnterQuestionsByLanguage(13L));

           seedTestFor(Turkish, topicFamily, getFamilyQuestionsByLanguage(14L));
           seedTestFor(Turkish, topicWork, getWorkQuestionsByLanguage(14L));
           seedTestFor(Turkish, topicEducation, getEduQuestionsByLanguage(14L));
//           seedTestFor(Turkish, topicMusic, getMusicQuestionsByLanguage(14L));
//           seedTestFor(Turkish, topicEntertainment, getEnterQuestionsByLanguage(14L));

           seedTestFor(Dutch, topicFamily, getFamilyQuestionsByLanguage(15L));
           seedTestFor(Dutch, topicWork, getWorkQuestionsByLanguage(15L));
           seedTestFor(Dutch, topicEducation, getEduQuestionsByLanguage(15L));
//           seedTestFor(Dutch, topicMusic, getMusicQuestionsByLanguage(15L));
//           seedTestFor(Dutch, topicEntertainment, getEnterQuestionsByLanguage(15L));

           seedTestFor(Thai, topicFamily, getFamilyQuestionsByLanguage(16L));
           seedTestFor(Thai, topicWork, getWorkQuestionsByLanguage(16L));
           seedTestFor(Thai, topicEducation, getEduQuestionsByLanguage(16L));
//           seedTestFor(Thai, topicMusic, getMusicQuestionsByLanguage(16L));
//           seedTestFor(Thai, topicEntertainment, getEnterQuestionsByLanguage(16L));
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }
    // English Version
    private String[][] familyEnglish() {
        String[][] res = {
                {
                        "What is a family?",
                        "A group of people living together in one place",
                        "A group of people who are friends",
                        "A collection of people bonded by marriage, blood relations, or adoption",
                        "A free social organization",
                        "A collection of people bonded by marriage, blood relations, or adoption"
                },
                {
                        "Family is mainly formed based on which relationship?",
                        "Economic relationship",
                        "Blood relations, marriage, adoption relationship",
                        "Social relationship",
                        "Friendship relationship",
                        "Blood relations, marriage, adoption relationship"
                },
                {
                        "Why is family considered the cell of society?",
                        "Because family has many people",
                        "Because family has existed for a long time",
                        "Because family is the foundation for forming society",
                        "Because family manages society",
                        "Because family is the foundation for forming society"
                },
                {
                        "Which of the following is NOT a function of family?",
                        "Reproductive function",
                        "Educational function",
                        "Economic function",
                        "Military function",
                        "Military function"
                },
                {
                        "Where is the educational function of family manifested?",
                        "Teaching scientific knowledge",
                        "Forming personality and morality",
                        "Managing the state",
                        "Developing technology",
                        "Forming personality and morality"
                },
                {
                        "What is the most important role of family for children?",
                        "Providing money",
                        "Education and care",
                        "Managing time",
                        "Controlling behavior",
                        "Education and care"
                },
                {
                        "A happy family is a family with what characteristics?",
                        "Wealthy",
                        "Many children",
                        "Members love and respect each other",
                        "High social status",
                        "Members love and respect each other"
                },
                {
                        "Which factor directly affects family happiness?",
                        "Care and sharing",
                        "Number of members",
                        "Housing area",
                        "Occupation",
                        "Care and sharing"
                },
                {
                        "What is the responsibility of parents towards their children?",
                        "Controlling all behaviors",
                        "Nurturing, educating and protecting",
                        "Only providing financial support",
                        "Making decisions for children",
                        "Nurturing, educating and protecting"
                },
                {
                        "What is the responsibility of children towards their parents?",
                        "Absolute obedience",
                        "Complete dependence",
                        "Respect and filial piety",
                        "Making decisions for parents",
                        "Respect and filial piety"
                },
                {
                        "Traditional families usually have what characteristics?",
                        "Few generations",
                        "Absolutely equal relationships",
                        "Multiple generations living together",
                        "No parental roles",
                        "Multiple generations living together"
                },
                {
                        "Modern families have what characteristics?",
                        "Multiple generations",
                        "Imposing relationships",
                        "More equal and democratic",
                        "Dependent on relatives",
                        "More equal and democratic"
                },
                {
                        "Relationships in family should be based on which principle?",
                        "Imposition",
                        "Personal interests",
                        "Love and respect",
                        "Power",
                        "Love and respect"
                },
                {
                        "What is the common cause leading to family conflicts?",
                        "Lack of communication and sharing",
                        "Cramped housing",
                        "Many children",
                        "Different occupations",
                        "Lack of communication and sharing"
                },
                {
                        "What role does family play in preserving culture?",
                        "Inventing culture",
                        "Passing on values and traditions",
                        "Managing society",
                        "Controlling people",
                        "Passing on values and traditions"
                },
                {
                        "Family influences individual personality through which factor?",
                        "Living environment and education",
                        "Money",
                        "Social status",
                        "Power",
                        "Living environment and education"
                },
                {
                        "What is the common responsibility of family members?",
                        "Only caring for oneself",
                        "Sharing and helping each other",
                        "Depending on others",
                        "Avoiding responsibility",
                        "Sharing and helping each other"
                },
                {
                        "What role does family play in modern society?",
                        "No longer important",
                        "Only personal in nature",
                        "Foundation for social stability",
                        "Only serving economy",
                        "Foundation for social stability"
                },
                {
                        "What is the manifestation of a happy family?",
                        "Wealthy",
                        "Few conflicts",
                        "Love and bonding",
                        "Having many assets",
                        "Love and bonding"
                },
                {
                        "What is the most important thing to build a happy family?",
                        "Money",
                        "Power",
                        "Love and respect",
                        "Social status",
                        "Love and respect"
                }
        };
        return res;
    }
    // Spanish Version - Versión en Español
    private String[][] familySpanish() {
        String[][] res = {
                {
                        "¿Qué es una familia?",
                        "Un grupo de personas que viven juntas en un lugar",
                        "Un grupo de personas que son amigas",
                        "Un conjunto de personas unidas por matrimonio, vínculos de sangre o adopción",
                        "Una organización social libre",
                        "Un conjunto de personas unidas por matrimonio, vínculos de sangre o adopción"
                },
                {
                        "¿La familia se forma principalmente basada en qué relación?",
                        "Relación económica",
                        "Relaciones de sangre, matrimonio, adopción",
                        "Relación social",
                        "Relación de amistad",
                        "Relaciones de sangre, matrimonio, adopción"
                },
                {
                        "¿Por qué se considera la familia como la célula de la sociedad?",
                        "Porque la familia tiene muchas personas",
                        "Porque la familia ha existido durante mucho tiempo",
                        "Porque la familia es la base para formar la sociedad",
                        "Porque la familia administra la sociedad",
                        "Porque la familia es la base para formar la sociedad"
                },
                {
                        "¿Cuál de las siguientes NO es una función de la familia?",
                        "Función reproductiva",
                        "Función educativa",
                        "Función económica",
                        "Función militar",
                        "Función militar"
                },
                {
                        "¿Dónde se manifiesta la función educativa de la familia?",
                        "Enseñando conocimiento científico",
                        "Formando personalidad y moralidad",
                        "Administrando el estado",
                        "Desarrollando tecnología",
                        "Formando personalidad y moralidad"
                },
                {
                        "¿Cuál es el papel más importante de la familia para los niños?",
                        "Proporcionar dinero",
                        "Educación y cuidado",
                        "Administrar el tiempo",
                        "Controlar el comportamiento",
                        "Educación y cuidado"
                },
                {
                        "¿Una familia feliz es una familia con qué características?",
                        "Adinerada",
                        "Muchos hijos",
                        "Los miembros se aman y se respetan mutuamente",
                        "Alto estatus social",
                        "Los miembros se aman y se respetan mutuamente"
                },
                {
                        "¿Qué factor afecta directamente la felicidad familiar?",
                        "Cuidado y compartir",
                        "Número de miembros",
                        "Área de vivienda",
                        "Ocupación",
                        "Cuidado y compartir"
                },
                {
                        "¿Cuál es la responsabilidad de los padres hacia sus hijos?",
                        "Controlar todos los comportamientos",
                        "Nutrir, educar y proteger",
                        "Solo proporcionar apoyo financiero",
                        "Tomar decisiones por los hijos",
                        "Nutrir, educar y proteger"
                },
                {
                        "¿Cuál es la responsabilidad de los hijos hacia sus padres?",
                        "Obediencia absoluta",
                        "Dependencia completa",
                        "Respeto y piedad filial",
                        "Tomar decisiones por los padres",
                        "Respeto y piedad filial"
                },
                {
                        "¿Las familias tradicionales usualmente tienen qué características?",
                        "Pocas generaciones",
                        "Relaciones absolutamente igualitarias",
                        "Múltiples generaciones viviendo juntas",
                        "Sin roles parentales",
                        "Múltiples generaciones viviendo juntas"
                },
                {
                        "¿Las familias modernas tienen qué características?",
                        "Múltiples generaciones",
                        "Relaciones impositivas",
                        "Más igualitarias y democráticas",
                        "Dependientes de los parientes",
                        "Más igualitarias y democráticas"
                },
                {
                        "¿Las relaciones en la familia deben basarse en qué principio?",
                        "Imposición",
                        "Intereses personales",
                        "Amor y respeto",
                        "Poder",
                        "Amor y respeto"
                },
                {
                        "¿Cuál es la causa común que lleva a conflictos familiares?",
                        "Falta de comunicación y compartir",
                        "Vivienda reducida",
                        "Muchos hijos",
                        "Diferentes ocupaciones",
                        "Falta de comunicación y compartir"
                },
                {
                        "¿Qué papel juega la familia en preservar la cultura?",
                        "Inventar cultura",
                        "Transmitir valores y tradiciones",
                        "Administrar la sociedad",
                        "Controlar a las personas",
                        "Transmitir valores y tradiciones"
                },
                {
                        "¿La familia influye en la personalidad individual a través de qué factor?",
                        "Ambiente de vida y educación",
                        "Dinero",
                        "Estatus social",
                        "Poder",
                        "Ambiente de vida y educación"
                },
                {
                        "¿Cuál es la responsabilidad común de los miembros de la familia?",
                        "Solo cuidar de uno mismo",
                        "Compartir y ayudarse mutuamente",
                        "Depender de otros",
                        "Evitar responsabilidades",
                        "Compartir y ayudarse mutuamente"
                },
                {
                        "¿Qué papel juega la familia en la sociedad moderna?",
                        "Ya no es importante",
                        "Solo de naturaleza personal",
                        "Base para la estabilidad social",
                        "Solo sirve a la economía",
                        "Base para la estabilidad social"
                },
                {
                        "¿Cuál es la manifestación de una familia feliz?",
                        "Adinerada",
                        "Pocos conflictos",
                        "Amor y vínculos",
                        "Tener muchos bienes",
                        "Amor y vínculos"
                },
                {
                        "¿Qué es lo más importante para construir una familia feliz?",
                        "Dinero",
                        "Poder",
                        "Amor y respeto",
                        "Estatus social",
                        "Amor y respeto"
                }
        };
        return res;
    }

    // French Version - Version Française
    private String[][] familyFrench() {
        String[][] res = {
                {
                        "Qu'est-ce qu'une famille ?",
                        "Un groupe de personnes vivant ensemble au même endroit",
                        "Un groupe de personnes qui sont amies",
                        "Un ensemble de personnes liées par le mariage, les liens du sang ou l'adoption",
                        "Une organisation sociale libre",
                        "Un ensemble de personnes liées par le mariage, les liens du sang ou l'adoption"
                },
                {
                        "La famille est principalement formée sur quelle base de relation ?",
                        "Relation économique",
                        "Relations de sang, mariage, adoption",
                        "Relation sociale",
                        "Relation d'amitié",
                        "Relations de sang, mariage, adoption"
                },
                {
                        "Pourquoi la famille est-elle considérée comme la cellule de la société ?",
                        "Parce que la famille a beaucoup de personnes",
                        "Parce que la famille existe depuis longtemps",
                        "Parce que la famille est la base pour former la société",
                        "Parce que la famille gère la société",
                        "Parce que la famille est la base pour former la société"
                },
                {
                        "Laquelle des suivantes N'EST PAS une fonction de la famille ?",
                        "Fonction reproductive",
                        "Fonction éducative",
                        "Fonction économique",
                        "Fonction militaire",
                        "Fonction militaire"
                },
                {
                        "Où se manifeste la fonction éducative de la famille ?",
                        "Enseigner les connaissances scientifiques",
                        "Former la personnalité et la moralité",
                        "Gérer l'État",
                        "Développer la technologie",
                        "Former la personnalité et la moralité"
                },
                {
                        "Quel est le rôle le plus important de la famille pour les enfants ?",
                        "Fournir de l'argent",
                        "Éducation et soins",
                        "Gérer le temps",
                        "Contrôler le comportement",
                        "Éducation et soins"
                },
                {
                        "Une famille heureuse est une famille avec quelles caractéristiques ?",
                        "Riche",
                        "Beaucoup d'enfants",
                        "Les membres s'aiment et se respectent mutuellement",
                        "Statut social élevé",
                        "Les membres s'aiment et se respectent mutuellement"
                },
                {
                        "Quel facteur affecte directement le bonheur familial ?",
                        "Attention et partage",
                        "Nombre de membres",
                        "Surface d'habitation",
                        "Profession",
                        "Attention et partage"
                },
                {
                        "Quelle est la responsabilité des parents envers leurs enfants ?",
                        "Contrôler tous les comportements",
                        "Nourrir, éduquer et protéger",
                        "Seulement fournir un soutien financier",
                        "Prendre des décisions pour les enfants",
                        "Nourrir, éduquer et protéger"
                },
                {
                        "Quelle est la responsabilité des enfants envers leurs parents ?",
                        "Obéissance absolue",
                        "Dépendance complète",
                        "Respect et piété filiale",
                        "Prendre des décisions pour les parents",
                        "Respect et piété filiale"
                },
                {
                        "Les familles traditionnelles ont généralement quelles caractéristiques ?",
                        "Peu de générations",
                        "Relations absolument égalitaires",
                        "Plusieurs générations vivant ensemble",
                        "Pas de rôles parentaux",
                        "Plusieurs générations vivant ensemble"
                },
                {
                        "Les familles modernes ont quelles caractéristiques ?",
                        "Plusieurs générations",
                        "Relations imposantes",
                        "Plus égalitaires et démocratiques",
                        "Dépendantes des parents",
                        "Plus égalitaires et démocratiques"
                },
                {
                        "Les relations dans la famille doivent être basées sur quel principe ?",
                        "Imposition",
                        "Intérêts personnels",
                        "Amour et respect",
                        "Pouvoir",
                        "Amour et respect"
                },
                {
                        "Quelle est la cause commune menant aux conflits familiaux ?",
                        "Manque de communication et de partage",
                        "Logement exigu",
                        "Beaucoup d'enfants",
                        "Différentes professions",
                        "Manque de communication et de partage"
                },
                {
                        "Quel rôle joue la famille dans la préservation de la culture ?",
                        "Inventer la culture",
                        "Transmettre les valeurs et traditions",
                        "Gérer la société",
                        "Contrôler les gens",
                        "Transmettre les valeurs et traditions"
                },
                {
                        "La famille influence la personnalité individuelle par quel facteur ?",
                        "Environnement de vie et éducation",
                        "Argent",
                        "Statut social",
                        "Pouvoir",
                        "Environnement de vie et éducation"
                },
                {
                        "Quelle est la responsabilité commune des membres de la famille ?",
                        "Ne s'occuper que de soi-même",
                        "Partager et s'entraider",
                        "Dépendre des autres",
                        "Éviter les responsabilités",
                        "Partager et s'entraider"
                },
                {
                        "Quel rôle joue la famille dans la société moderne ?",
                        "N'est plus important",
                        "Seulement de nature personnelle",
                        "Base pour la stabilité sociale",
                        "Sert seulement l'économie",
                        "Base pour la stabilité sociale"
                },
                {
                        "Quelle est la manifestation d'une famille heureuse ?",
                        "Riche",
                        "Peu de conflits",
                        "Amour et liens",
                        "Avoir beaucoup de biens",
                        "Amour et liens"
                },
                {
                        "Quelle est la chose la plus importante pour construire une famille heureuse ?",
                        "Argent",
                        "Pouvoir",
                        "Amour et respect",
                        "Statut social",
                        "Amour et respect"
                }
        };
        return res;
    }





    // German Version - Deutsche Version
    private String[][] familyGerman() {
        String[][] res = {
                {
                        "Was ist eine Familie?",
                        "Eine Gruppe von Menschen, die zusammen an einem Ort leben",
                        "Eine Gruppe von Menschen, die befreundet sind",
                        "Eine Sammlung von Menschen, die durch Ehe, Blutsverwandtschaft oder Adoption verbunden sind",
                        "Eine freie gesellschaftliche Organisation",
                        "Eine Sammlung von Menschen, die durch Ehe, Blutsverwandtschaft oder Adoption verbunden sind"
                },
                {
                        "Familie wird hauptsächlich auf welcher Beziehung basierend gebildet?",
                        "Wirtschaftliche Beziehung",
                        "Blutsverwandtschaft, Ehe, Adoptionsbeziehung",
                        "Gesellschaftliche Beziehung",
                        "Freundschaftsbeziehung",
                        "Blutsverwandtschaft, Ehe, Adoptionsbeziehung"
                },
                {
                        "Warum wird Familie als Zelle der Gesellschaft betrachtet?",
                        "Weil Familie viele Menschen hat",
                        "Weil Familie schon lange existiert",
                        "Weil Familie die Grundlage für die Gesellschaftsbildung ist",
                        "Weil Familie die Gesellschaft verwaltet",
                        "Weil Familie die Grundlage für die Gesellschaftsbildung ist"
                },
                {
                        "Welches der folgenden ist KEINE Funktion der Familie?",
                        "Fortpflanzungsfunktion",
                        "Bildungsfunktion",
                        "Wirtschaftsfunktion",
                        "Militärfunktion",
                        "Militärfunktion"
                },
                {
                        "Wo manifestiert sich die Bildungsfunktion der Familie?",
                        "Wissenschaftliches Wissen lehren",
                        "Persönlichkeit und Moral formen",
                        "Den Staat verwalten",
                        "Technologie entwickeln",
                        "Persönlichkeit und Moral formen"
                },
                {
                        "Was ist die wichtigste Rolle der Familie für Kinder?",
                        "Geld bereitstellen",
                        "Bildung und Fürsorge",
                        "Zeit verwalten",
                        "Verhalten kontrollieren",
                        "Bildung und Fürsorge"
                },
                {
                        "Eine glückliche Familie ist eine Familie mit welchen Eigenschaften?",
                        "Wohlhabend",
                        "Viele Kinder",
                        "Mitglieder lieben und respektieren sich gegenseitig",
                        "Hoher gesellschaftlicher Status",
                        "Mitglieder lieben und respektieren sich gegenseitig"
                },
                {
                        "Welcher Faktor beeinflusst direkt das Familienglück?",
                        "Fürsorge und Teilen",
                        "Anzahl der Mitglieder",
                        "Wohnfläche",
                        "Beruf",
                        "Fürsorge und Teilen"
                },
                {
                        "Was ist die Verantwortung der Eltern gegenüber ihren Kindern?",
                        "Alle Verhaltensweisen kontrollieren",
                        "Nähren, erziehen und schützen",
                        "Nur finanzielle Unterstützung bieten",
                        "Entscheidungen für Kinder treffen",
                        "Nähren, erziehen und schützen"
                },
                {
                        "Was ist die Verantwortung der Kinder gegenüber ihren Eltern?",
                        "Absoluter Gehorsam",
                        "Vollständige Abhängigkeit",
                        "Respekt und kindliche Pietät",
                        "Entscheidungen für Eltern treffen",
                        "Respekt und kindliche Pietät"
                },
                {
                        "Traditionelle Familien haben normalerweise welche Eigenschaften?",
                        "Wenige Generationen",
                        "Absolut gleichberechtigte Beziehungen",
                        "Mehrere Generationen leben zusammen",
                        "Keine elterlichen Rollen",
                        "Mehrere Generationen leben zusammen"
                },
                {
                        "Moderne Familien haben welche Eigenschaften?",
                        "Mehrere Generationen",
                        "Aufdrängende Beziehungen",
                        "Gleichberechtigter und demokratischer",
                        "Abhängig von Verwandten",
                        "Gleichberechtigter und demokratischer"
                },
                {
                        "Beziehungen in der Familie sollten auf welchem Prinzip basieren?",
                        "Aufdrängung",
                        "Persönliche Interessen",
                        "Liebe und Respekt",
                        "Macht",
                        "Liebe und Respekt"
                },
                {
                        "Was ist die häufige Ursache, die zu Familienkonflikten führt?",
                        "Mangel an Kommunikation und Teilen",
                        "Beengte Wohnung",
                        "Viele Kinder",
                        "Verschiedene Berufe",
                        "Mangel an Kommunikation und Teilen"
                },
                {
                        "Welche Rolle spielt Familie bei der Kulturerhaltung?",
                        "Kultur erfinden",
                        "Werte und Traditionen weitergeben",
                        "Gesellschaft verwalten",
                        "Menschen kontrollieren",
                        "Werte und Traditionen weitergeben"
                },
                {
                        "Familie beeinflusst individuelle Persönlichkeit durch welchen Faktor?",
                        "Lebensumfeld und Bildung",
                        "Geld",
                        "Gesellschaftlicher Status",
                        "Macht",
                        "Lebensumfeld und Bildung"
                },
                {
                        "Was ist die gemeinsame Verantwortung der Familienmitglieder?",
                        "Nur für sich selbst sorgen",
                        "Teilen und sich gegenseitig helfen",
                        "Von anderen abhängen",
                        "Verantwortung vermeiden",
                        "Teilen und sich gegenseitig helfen"
                },
                {
                        "Welche Rolle spielt Familie in der modernen Gesellschaft?",
                        "Nicht mehr wichtig",
                        "Nur persönlicher Natur",
                        "Grundlage für gesellschaftliche Stabilität",
                        "Dient nur der Wirtschaft",
                        "Grundlage für gesellschaftliche Stabilität"
                },
                {
                        "Was ist die Manifestation einer glücklichen Familie?",
                        "Wohlhabend",
                        "Wenige Konflikte",
                        "Liebe und Bindung",
                        "Viele Vermögenswerte haben",
                        "Liebe und Bindung"
                },
                {
                        "Was ist das Wichtigste, um eine glückliche Familie aufzubauen?",
                        "Geld",
                        "Macht",
                        "Liebe und Respekt",
                        "Gesellschaftlicher Status",
                        "Liebe und Respekt"
                }
        };
        return res;
    }

    // Chinese Version - 中文版本
    private String[][] familyChinese() {
        String[][] res = {
                {
                        "什么是家庭？",
                        "住在同一个地方的一群人",
                        "一群朋友关系的人",
                        "通过婚姻、血缘关系或收养联系在一起的人群",
                        "自由的社会组织",
                        "通过婚姻、血缘关系或收养联系在一起的人群"
                },
                {
                        "家庭主要基于什么关系形成？",
                        "经济关系",
                        "血缘、婚姻、收养关系",
                        "社会关系",
                        "朋友关系",
                        "血缘、婚姻、收养关系"
                },
                {
                        "为什么家庭被认为是社会的细胞？",
                        "因为家庭人多",
                        "因为家庭存在时间长",
                        "因为家庭是构成社会的基础",
                        "因为家庭管理社会",
                        "因为家庭是构成社会的基础"
                },
                {
                        "以下哪个不是家庭的功能？",
                        "生殖功能",
                        "教育功能",
                        "经济功能",
                        "军事功能",
                        "军事功能"
                },
                {
                        "家庭的教育功能体现在哪里？",
                        "教授科学知识",
                        "形成人格和道德",
                        "管理国家",
                        "发展技术",
                        "形成人格和道德"
                },
                {
                        "家庭对儿童最重要的作用是什么？",
                        "提供金钱",
                        "教育和照顾",
                        "管理时间",
                        "控制行为",
                        "教育和照顾"
                },
                {
                        "幸福的家庭是具有什么特征的家庭？",
                        "富有",
                        "孩子多",
                        "成员之间相爱相敬",
                        "社会地位高",
                        "成员之间相爱相敬"
                },
                {
                        "什么因素直接影响家庭幸福？",
                        "关心和分享",
                        "成员数量",
                        "住房面积",
                        "职业",
                        "关心和分享"
                },
                {
                        "父母对子女的责任是什么？",
                        "控制所有行为",
                        "养育、教育和保护",
                        "只提供经济支持",
                        "替子女做决定",
                        "养育、教育和保护"
                },
                {
                        "子女对父母的责任是什么？",
                        "绝对服从",
                        "完全依赖",
                        "尊敬、孝顺",
                        "替父母做决定",
                        "尊敬、孝顺"
                },
                {
                        "传统家庭通常具有什么特征？",
                        "世代少",
                        "绝对平等的关系",
                        "多世代共同生活",
                        "没有父母角色",
                        "多世代共同生活"
                },
                {
                        "现代家庭具有什么特征？",
                        "多世代",
                        "强制性关系",
                        "更加平等和民主",
                        "依赖亲戚",
                        "更加平等和民主"
                },
                {
                        "家庭中的关系应该建立在什么原则基础上？",
                        "强制",
                        "个人利益",
                        "爱和尊重",
                        "权力",
                        "爱和尊重"
                },
                {
                        "导致家庭矛盾的常见原因是什么？",
                        "缺乏沟通和分享",
                        "住房拥挤",
                        "孩子多",
                        "职业不同",
                        "缺乏沟通和分享"
                },
                {
                        "家庭在保护文化方面起什么作用？",
                        "发明文化",
                        "传承价值观和传统",
                        "管理社会",
                        "控制人们",
                        "传承价值观和传统"
                },
                {
                        "家庭通过什么因素影响个人性格？",
                        "生活环境和教育",
                        "金钱",
                        "社会地位",
                        "权力",
                        "生活环境和教育"
                },
                {
                        "家庭成员的共同责任是什么？",
                        "只照顾自己",
                        "分享和互相帮助",
                        "依靠他人",
                        "逃避责任",
                        "分享和互相帮助"
                },
                {
                        "家庭在现代社会中起什么作用？",
                        "不再重要",
                        "仅具有个人性质",
                        "社会稳定的基础",
                        "仅服务经济",
                        "社会稳定的基础"
                },
                {
                        "幸福家庭的表现是什么？",
                        "富有",
                        "矛盾少",
                        "爱和团结",
                        "拥有很多财产",
                        "爱和团结"
                },
                {
                        "建设幸福家庭最重要的是什么？",
                        "金钱",
                        "权力",
                        "爱和尊重",
                        "社会地位",
                        "爱和尊重"
                }
        };
        return res;
    }

    // Korean Version - 한국어 버전
    private String[][] familyKorean() {
        String[][] res = {
                {
                        "가족이란 무엇입니까?",
                        "한 곳에서 함께 사는 사람들의 그룹",
                        "친구 관계에 있는 사람들의 그룹",
                        "결혼, 혈연관계 또는 입양으로 결합된 사람들의 집합",
                        "자유로운 사회 조직",
                        "결혼, 혈연관계 또는 입양으로 결합된 사람들의 집합"
                },
                {
                        "가족은 주로 어떤 관계를 바탕으로 형성됩니까?",
                        "경제적 관계",
                        "혈연, 결혼, 입양 관계",
                        "사회적 관계",
                        "친구 관계",
                        "혈연, 결혼, 입양 관계"
                },
                {
                        "왜 가족이 사회의 세포로 여겨집니까?",
                        "가족은 사람이 많기 때문에",
                        "가족은 오랫동안 존재해왔기 때문에",
                        "가족이 사회를 형성하는 기초이기 때문에",
                        "가족이 사회를 관리하기 때문에",
                        "가족이 사회를 형성하는 기초이기 때문에"
                },
                {
                        "다음 중 가족의 기능이 아닌 것은 무엇입니까?",
                        "생식 기능",
                        "교육 기능",
                        "경제적 기능",
                        "군사적 기능",
                        "군사적 기능"
                },
                {
                        "가족의 교육 기능은 어디에서 나타납니까?",
                        "과학 지식을 가르치는 것",
                        "인격과 도덕을 형성하는 것",
                        "국가를 관리하는 것",
                        "기술을 발전시키는 것",
                        "인격과 도덕을 형성하는 것"
                },
                {
                        "아이들에 대한 가족의 가장 중요한 역할은 무엇입니까?",
                        "돈을 제공하는 것",
                        "교육과 돌봄",
                        "시간을 관리하는 것",
                        "행동을 통제하는 것",
                        "교육과 돌봄"
                },
                {
                        "행복한 가족은 어떤 특징을 가진 가족입니까?",
                        "부유한",
                        "자녀가 많은",
                        "구성원들이 서로 사랑하고 존중하는",
                        "사회적 지위가 높은",
                        "구성원들이 서로 사랑하고 존중하는"
                },
                {
                        "가족의 행복에 직접적으로 영향을 미치는 요인은 무엇입니까?",
                        "관심과 나눔",
                        "구성원의 수",
                        "주거 면적",
                        "직업",
                        "관심과 나눔"
                },
                {
                        "부모의 자녀에 대한 책임은 무엇입니까?",
                        "모든 행동을 통제하는 것",
                        "양육, 교육, 보호하는 것",
                        "경제적 지원만 제공하는 것",
                        "자녀를 대신해 결정하는 것",
                        "양육, 교육, 보호하는 것"
                },
                {
                        "자녀의 부모에 대한 책임은 무엇입니까?",
                        "절대적 복종",
                        "완전한 의존",
                        "존경과 효도",
                        "부모를 대신해 결정하는 것",
                        "존경과 효도"
                },
                {
                        "전통적인 가족은 보통 어떤 특징을 가집니까?",
                        "세대가 적은",
                        "절대적으로 평등한 관계",
                        "여러 세대가 함께 사는",
                        "부모 역할이 없는",
                        "여러 세대가 함께 사는"
                },
                {
                        "현대 가족은 어떤 특징을 가집니까?",
                        "여러 세대",
                        "강압적인 관계",
                        "더 평등하고 민주적인",
                        "친척에 의존하는",
                        "더 평등하고 민주적인"
                },
                {
                        "가족의 관계는 어떤 원칙에 기초해야 합니까?",
                        "강요",
                        "개인적 이익",
                        "사랑과 존중",
                        "권력",
                        "사랑과 존중"
                },
                {
                        "가족 갈등으로 이어지는 일반적인 원인은 무엇입니까?",
                        "소통과 나눔의 부족",
                        "좁은 주거 공간",
                        "자녀가 많은 것",
                        "다른 직업",
                        "소통과 나눔의 부족"
                },
                {
                        "가족이 문화 보존에서 하는 역할은 무엇입니까?",
                        "문화를 발명하는 것",
                        "가치와 전통을 전승하는 것",
                        "사회를 관리하는 것",
                        "사람들을 통제하는 것",
                        "가치와 전통을 전승하는 것"
                },
                {
                        "가족은 어떤 요인을 통해 개인의 인격에 영향을 미칩니까?",
                        "생활 환경과 교육",
                        "돈",
                        "사회적 지위",
                        "권력",
                        "생활 환경과 교육"
                },
                {
                        "가족 구성원들의 공통된 책임은 무엇입니까?",
                        "자신만 돌보는 것",
                        "나누고 서로 도우는 것",
                        "다른 사람에게 의존하는 것",
                        "책임을 피하는 것",
                        "나누고 서로 도우는 것"
                },
                {
                        "현대 사회에서 가족은 어떤 역할을 합니까?",
                        "더 이상 중요하지 않은",
                        "오직 개인적인 성격만의",
                        "사회 안정의 기초",
                        "오직 경제에만 봉사하는",
                        "사회 안정의 기초"
                },
                {
                        "행복한 가족의 표현은 무엇입니까?",
                        "부유한",
                        "갈등이 적은",
                        "사랑과 유대",
                        "많은 재산을 가진",
                        "사랑과 유대"
                },
                {
                        "행복한 가족을 건설하는 데 가장 중요한 것은 무엇입니까?",
                        "돈",
                        "권력",
                        "사랑과 존중",
                        "사회적 지위",
                        "사랑과 존중"
                }
        };
        return res;
    }

    // Hindi Version - हिन्दी संस्करण
    private String[][] familyHindi() {
        String[][] res = {
                {
                        "परिवार क्या है?",
                        "एक जगह पर एक साथ रहने वाले लोगों का समूह",
                        "मित्र संबंध वाले लोगों का समूह",
                        "विवाह, रक्त संबंध या गोद लेने से जुड़े लोगों का संग्रह",
                        "एक स्वतंत्र सामाजिक संगठन",
                        "विवाह, रक्त संबंध या गोद लेने से जुड़े लोगों का संग्रह"
                },
                {
                        "परिवार मुख्यतः किस संबंध के आधार पर बनता है?",
                        "आर्थिक संबंध",
                        "रक्त, विवाह, गोद लेने का संबंध",
                        "सामाजिक संबंध",
                        "मित्र संबंध",
                        "रक्त, विवाह, गोद लेने का संबंध"
                },
                {
                        "परिवार को समाज की कोशिका क्यों माना जाता है?",
                        "क्योंकि परिवार में बहुत लोग होते हैं",
                        "क्योंकि परिवार लंबे समय से अस्तित्व में है",
                        "क्योंकि परिवार समाज निर्माण की आधारशिला है",
                        "क्योंकि परिवार समाज का प्रबंधन करता है",
                        "क्योंकि परिवार समाज निर्माण की आधारशिला है"
                },
                {
                        "निम्न में से कौन सा परिवार का कार्य नहीं है?",
                        "प्रजनन कार्य",
                        "शिक्षा कार्य",
                        "आर्थिक कार्य",
                        "सैन्य कार्य",
                        "सैन्य कार्य"
                },
                {
                        "परिवार का शिक्षा कार्य कहाँ प्रकट होता है?",
                        "वैज्ञानिक ज्ञान सिखाना",
                        "व्यक्तित्व और नैतिकता का निर्माण",
                        "राज्य का प्रबंधन",
                        "प्रौद्योगिकी का विकास",
                        "व्यक्तित्व और नैतिकता का निर्माण"
                },
                {
                        "बच्चों के लिए परिवार की सबसे महत्वपूर्ण भूमिका क्या है?",
                        "पैसा प्रदान करना",
                        "शिक्षा और देखभाल",
                        "समय का प्रबंधन",
                        "व्यवहार को नियंत्रित करना",
                        "शिक्षा और देखभाल"
                },
                {
                        "खुशहाल परिवार किन विशेषताओं वाला परिवार है?",
                        "धनी",
                        "अधिक बच्चे वाला",
                        "सदस्य एक-दूसरे से प्रेम और सम्मान करते हैं",
                        "उच्च सामाजिक स्थिति वाला",
                        "सदस्य एक-दूसरे से प्रेम और सम्मान करते हैं"
                },
                {
                        "कौन सा कारक पारिवारिक खुशी को सीधे प्रभावित करता है?",
                        "देखभाल और साझाकरण",
                        "सदस्यों की संख्या",
                        "आवास का क्षेत्रफल",
                        "व्यवसाय",
                        "देखभाल और साझाकरण"
                },
                {
                        "माता-पिता की अपने बच्चों के प्रति जिम्मेदारी क्या है?",
                        "सभी व्यवहारों को नियंत्रित करना",
                        "पालन-पोषण, शिक्षा और सुरक्षा",
                        "केवल आर्थिक सहायता प्रदान करना",
                        "बच्चों की ओर से निर्णय लेना",
                        "पालन-पोषण, शिक्षा और सुरक्षा"
                },
                {
                        "बच्चों की अपने माता-पिता के प्रति जिम्मेदारी क्या है?",
                        "पूर्ण आज्ञाकारिता",
                        "पूर्ण निर्भरता",
                        "सम्मान और श्रद्धा",
                        "माता-पिता की ओर से निर्णय लेना",
                        "सम्मान और श्रद्धा"
                },
                {
                        "पारंपरिक परिवार आमतौर पर किन विशेषताओं के होते हैं?",
                        "कम पीढ़ियों के",
                        "पूर्णतः समान संबंधों के",
                        "कई पीढ़ियाँ एक साथ रहती हैं",
                        "माता-पिता की भूमिका नहीं",
                        "कई पीढ़ियाँ एक साथ रहती हैं"
                },
                {
                        "आधुनिक परिवार की क्या विशेषताएं हैं?",
                        "कई पीढ़ियाँ",
                        "दबावपूर्ण संबंध",
                        "अधिक समान और लोकतांत्रिक",
                        "रिश्तेदारों पर निर्भर",
                        "अधिक समान और लोकतांत्रिक"
                },
                {
                        "परिवार में संबंध किस सिद्धांत पर आधारित होने चाहिए?",
                        "दबाव",
                        "व्यक्तिगत हित",
                        "प्रेम और सम्मान",
                        "शक्ति",
                        "प्रेम और सम्मान"
                },
                {
                        "पारिवारिक संघर्ष का सामान्य कारण क्या है?",
                        "संवाद और साझाकरण की कमी",
                        "तंग आवास",
                        "अधिक बच्चे",
                        "अलग व्यवसाय",
                        "संवाद और साझाकरण की कमी"
                },
                {
                        "संस्कृति के संरक्षण में परिवार की क्या भूमिका है?",
                        "संस्कृति का आविष्कार",
                        "मूल्यों और परंपराओं को आगे बढ़ाना",
                        "समाज का प्रबंधन",
                        "लोगों को नियंत्रित करना",
                        "मूल्यों और परंपराओं को आगे बढ़ाना"
                },
                {
                        "परिवार किस कारक के माध्यम से व्यक्तिगत व्यक्तित्व को प्रभावित करता है?",
                        "जीवन वातावरण और शिक्षा",
                        "पैसा",
                        "सामाजिक स्थिति",
                        "शक्ति",
                        "जीवन वातावरण और शिक्षा"
                },
                {
                        "परिवार के सदस्यों की सामान्य जिम्मेदारी क्या है?",
                        "केवल अपनी देखभाल करना",
                        "साझाकरण और एक-दूसरे की सहायता",
                        "दूसरों पर निर्भर रहना",
                        "जिम्मेदारी से बचना",
                        "साझाकरण और एक-दूसरे की सहायता"
                },
                {
                        "आधुनिक समाज में परिवार की क्या भूमिका है?",
                        "अब महत्वपूर्ण नहीं",
                        "केवल व्यक्तिगत प्रकृति की",
                        "सामाजिक स्थिरता की नींव",
                        "केवल अर्थव्यवस्था की सेवा",
                        "सामाजिक स्थिरता की नींव"
                },
                {
                        "खुशहाल परिवार की अभिव्यक्ति क्या है?",
                        "धनी",
                        "कम संघर्ष",
                        "प्रेम और जुड़ाव",
                        "बहुत संपत्ति रखना",
                        "प्रेम और जुड़ाव"
                },
                {
                        "खुशहाल परिवार बनाने के लिए सबसे महत्वपूर्ण चीज क्या है?",
                        "पैसा",
                        "शक्ति",
                        "प्रेम और सम्मान",
                        "सामाजिक स्थिति",
                        "प्रेम और सम्मान"
                }
        };
        return res;
    }

    // Russian Version - Русская версия
    private String[][] familyRussian() {
        String[][] res = {
                {
                        "Что такое семья?",
                        "Группа людей, живущих вместе в одном месте",
                        "Группа людей, находящихся в дружеских отношениях",
                        "Совокупность людей, связанных браком, кровным родством или усыновлением",
                        "Свободная социальная организация",
                        "Совокупность людей, связанных браком, кровным родством или усыновлением"
                },
                {
                        "Семья формируется в основном на основе каких отношений?",
                        "Экономические отношения",
                        "Кровное родство, брак, отношения усыновления",
                        "Социальные отношения",
                        "Дружеские отношения",
                        "Кровное родство, брак, отношения усыновления"
                },
                {
                        "Почему семья считается ячейкой общества?",
                        "Потому что в семье много людей",
                        "Потому что семья существует уже долгое время",
                        "Потому что семья является основой формирования общества",
                        "Потому что семья управляет обществом",
                        "Потому что семья является основой формирования общества"
                },
                {
                        "Что из следующего НЕ является функцией семьи?",
                        "Репродуктивная функция",
                        "Образовательная функция",
                        "Экономическая функция",
                        "Военная функция",
                        "Военная функция"
                },
                {
                        "Где проявляется образовательная функция семьи?",
                        "Обучение научным знаниям",
                        "Формирование личности и морали",
                        "Управление государством",
                        "Развитие технологий",
                        "Формирование личности и морали"
                },
                {
                        "Какова самая важная роль семьи для детей?",
                        "Обеспечение деньгами",
                        "Образование и забота",
                        "Управление временем",
                        "Контроль поведения",
                        "Образование и забота"
                },
                {
                        "Счастливая семья - это семья с какими характеристиками?",
                        "Богатая",
                        "С большим количеством детей",
                        "Члены семьи любят и уважают друг друга",
                        "С высоким социальным статусом",
                        "Члены семьи любят и уважают друг друга"
                },
                {
                        "Какой фактор напрямую влияет на семейное счастье?",
                        "Забота и разделение",
                        "Количество членов семьи",
                        "Площадь жилья",
                        "Профессия",
                        "Забота и разделение"
                },
                {
                        "Какова ответственность родителей перед детьми?",
                        "Контролировать все поведение",
                        "Воспитывать, обучать и защищать",
                        "Только обеспечивать финансовую поддержку",
                        "Принимать решения за детей",
                        "Воспитывать, обучать и защищать"
                },
                {
                        "Какова ответственность детей перед родителями?",
                        "Абсолютное послушание",
                        "Полная зависимость",
                        "Уважение и почтение к родителям",
                        "Принятие решений за родителей",
                        "Уважение и почтение к родителям"
                },
                {
                        "Традиционные семьи обычно имеют какие характеристики?",
                        "Мало поколений",
                        "Абсолютно равные отношения",
                        "Несколько поколений живут вместе",
                        "Нет родительских ролей",
                        "Несколько поколений живут вместе"
                },
                {
                        "Современные семьи имеют какие характеристики?",
                        "Несколько поколений",
                        "Принудительные отношения",
                        "Более равные и демократичные",
                        "Зависимые от родственников",
                        "Более равные и демократичные"
                },
                {
                        "Отношения в семье должны основываться на каком принципе?",
                        "Принуждение",
                        "Личные интересы",
                        "Любовь и уважение",
                        "Власть",
                        "Любовь и уважение"
                },
                {
                        "Какова общая причина семейных конфликтов?",
                        "Недостаток общения и разделения",
                        "Тесное жилье",
                        "Много детей",
                        "Разные профессии",
                        "Недостаток общения и разделения"
                },
                {
                        "Какую роль играет семья в сохранении культуры?",
                        "Изобретение культуры",
                        "Передача ценностей и традиций",
                        "Управление обществом",
                        "Контроль людей",
                        "Передача ценностей и традиций"
                },
                {
                        "Семья влияет на индивидуальную личность через какой фактор?",
                        "Среда жизни и образование",
                        "Деньги",
                        "Социальный статус",
                        "Власть",
                        "Среда жизни и образование"
                },
                {
                        "Какова общая ответственность членов семьи?",
                        "Заботиться только о себе",
                        "Разделение и взаимопомощь",
                        "Зависеть от других",
                        "Избегать ответственности",
                        "Разделение и взаимопомощь"
                },
                {
                        "Какую роль играет семья в современном обществе?",
                        "Больше не важна",
                        "Только личного характера",
                        "Основа социальной стабильности",
                        "Служит только экономике",
                        "Основа социальной стабильности"
                },
                {
                        "Каково проявление счастливой семьи?",
                        "Богатая",
                        "Мало конфликтов",
                        "Любовь и связь",
                        "Много имущества",
                        "Любовь и связь"
                },
                {
                        "Что самое важное для построения счастливой семьи?",
                        "Деньги",
                        "Власть",
                        "Любовь и уважение",
                        "Социальный статус",
                        "Любовь и уважение"
                }
        };
        return res;
    }

    // Portuguese Version - Versão Portuguesa
    private String[][] familyPortuguese() {
        String[][] res = {
                {
                        "O que é uma família?",
                        "Um grupo de pessoas vivendo juntas em um lugar",
                        "Um grupo de pessoas que têm relações de amizade",
                        "Uma coleção de pessoas unidas por casamento, laços sanguíneos ou adoção",
                        "Uma organização social livre",
                        "Uma coleção de pessoas unidas por casamento, laços sanguíneos ou adoção"
                },
                {
                        "A família é formada principalmente com base em que relacionamento?",
                        "Relacionamento econômico",
                        "Laços sanguíneos, casamento, relacionamento de adoção",
                        "Relacionamento social",
                        "Relacionamento de amizade",
                        "Laços sanguíneos, casamento, relacionamento de adoção"
                },
                {
                        "Por que a família é considerada a célula da sociedade?",
                        "Porque a família tem muitas pessoas",
                        "Porque a família existe há muito tempo",
                        "Porque a família é a base para formar a sociedade",
                        "Porque a família administra a sociedade",
                        "Porque a família é a base para formar a sociedade"
                },
                {
                        "Qual das seguintes NÃO é uma função da família?",
                        "Função reprodutiva",
                        "Função educacional",
                        "Função econômica",
                        "Função militar",
                        "Função militar"
                },
                {
                        "Onde se manifesta a função educacional da família?",
                        "Ensinar conhecimento científico",
                        "Formar personalidade e moralidade",
                        "Administrar o estado",
                        "Desenvolver tecnologia",
                        "Formar personalidade e moralidade"
                },
                {
                        "Qual é o papel mais importante da família para as crianças?",
                        "Fornecer dinheiro",
                        "Educação e cuidado",
                        "Administrar o tempo",
                        "Controlar comportamento",
                        "Educação e cuidado"
                },
                {
                        "Uma família feliz é uma família com quais características?",
                        "Rica",
                        "Muitos filhos",
                        "Membros se amam e se respeitam mutuamente",
                        "Alto status social",
                        "Membros se amam e se respeitam mutuamente"
                },
                {
                        "Qual fator afeta diretamente a felicidade familiar?",
                        "Cuidado e compartilhamento",
                        "Número de membros",
                        "Área habitacional",
                        "Ocupação",
                        "Cuidado e compartilhamento"
                },
                {
                        "Qual é a responsabilidade dos pais em relação aos seus filhos?",
                        "Controlar todos os comportamentos",
                        "Nutrir, educar e proteger",
                        "Apenas fornecer apoio financeiro",
                        "Tomar decisões pelos filhos",
                        "Nutrir, educar e proteger"
                },
                {
                        "Qual é a responsabilidade dos filhos em relação aos seus pais?",
                        "Obediência absoluta",
                        "Dependência completa",
                        "Respeito e piedade filial",
                        "Tomar decisões pelos pais",
                        "Respeito e piedade filial"
                },
                {
                        "Famílias tradicionais geralmente têm quais características?",
                        "Poucas gerações",
                        "Relacionamentos absolutamente iguais",
                        "Várias gerações vivendo juntas",
                        "Sem papéis parentais",
                        "Várias gerações vivendo juntas"
                },
                {
                        "Famílias modernas têm quais características?",
                        "Várias gerações",
                        "Relacionamentos impostos",
                        "Mais igualitárias e democráticas",
                        "Dependentes de parentes",
                        "Mais igualitárias e democráticas"
                },
                {
                        "Relacionamentos na família devem ser baseados em qual princípio?",
                        "Imposição",
                        "Interesses pessoais",
                        "Amor e respeito",
                        "Poder",
                        "Amor e respeito"
                },
                {
                        "Qual é a causa comum que leva a conflitos familiares?",
                        "Falta de comunicação e compartilhamento",
                        "Habitação apertada",
                        "Muitos filhos",
                        "Ocupações diferentes",
                        "Falta de comunicação e compartilhamento"
                },
                {
                        "Que papel a família desempenha na preservação da cultura?",
                        "Inventar cultura",
                        "Passar valores e tradições",
                        "Administrar a sociedade",
                        "Controlar pessoas",
                        "Passar valores e tradições"
                },
                {
                        "A família influencia a personalidade individual através de qual fator?",
                        "Ambiente de vida e educação",
                        "Dinheiro",
                        "Status social",
                        "Poder",
                        "Ambiente de vida e educação"
                },
                {
                        "Qual é a responsabilidade comum dos membros da família?",
                        "Cuidar apenas de si mesmo",
                        "Compartilhar e ajudar uns aos outros",
                        "Depender de outros",
                        "Evitar responsabilidade",
                        "Compartilhar e ajudar uns aos outros"
                },
                {
                        "Que papel a família desempenha na sociedade moderna?",
                        "Não é mais importante",
                        "Apenas de natureza pessoal",
                        "Base para estabilidade social",
                        "Serve apenas à economia",
                        "Base para estabilidade social"
                },
                {
                        "Qual é a manifestação de uma família feliz?",
                        "Rica",
                        "Poucos conflitos",
                        "Amor e vínculo",
                        "Ter muitos bens",
                        "Amor e vínculo"
                },
                {
                        "Qual é a coisa mais importante para construir uma família feliz?",
                        "Dinheiro",
                        "Poder",
                        "Amor e respeito",
                        "Status social",
                        "Amor e respeito"
                }
        };
        return res;
    }

    // Japanese Version - 日本語版
    private String[][] familyJapanese() {
        String[][] res = {
                {
                        "家族とは何ですか？",
                        "同じ場所で一緒に住む人々のグループ",
                        "友人関係にある人々のグループ",
                        "結婚、血縁関係、養子縁組によって結ばれた人々の集まり",
                        "自由な社会組織",
                        "結婚、血縁関係、養子縁組によって結ばれた人々の集まり"
                },
                {
                        "家族は主にどの関係に基づいて形成されますか？",
                        "経済関係",
                        "血縁、結婚、養子縁組関係",
                        "社会関係",
                        "友人関係",
                        "血縁、結婚、養子縁組関係"
                },
                {
                        "なぜ家族は社会の細胞と考えられているのですか？",
                        "家族は人数が多いから",
                        "家族は長い間存在しているから",
                        "家族は社会を形成する基盤だから",
                        "家族は社会を管理するから",
                        "家族は社会を形成する基盤だから"
                },
                {
                        "以下のうち家族の機能でないものはどれですか？",
                        "生殖機能",
                        "教育機能",
                        "経済機能",
                        "軍事機能",
                        "軍事機能"
                },
                {
                        "家族の教育機能はどこに現れますか？",
                        "科学的知識を教える",
                        "人格と道徳を形成する",
                        "国家を管理する",
                        "技術を発展させる",
                        "人格と道徳を形成する"
                },
                {
                        "子どもにとって家族の最も重要な役割は何ですか？",
                        "お金を提供すること",
                        "教育とケア",
                        "時間を管理すること",
                        "行動をコントロールすること",
                        "教育とケア"
                },
                {
                        "幸せな家族はどのような特徴を持つ家族ですか？",
                        "裕福",
                        "子どもが多い",
                        "メンバーが互いを愛し尊敬し合う",
                        "社会的地位が高い",
                        "メンバーが互いを愛し尊敬し合う"
                },
                {
                        "家族の幸せに直接影響する要因は何ですか？",
                        "気遣いと分かち合い",
                        "メンバーの数",
                        "住宅面積",
                        "職業",
                        "気遣いと分かち合い"
                },
                {
                        "親の子どもに対する責任は何ですか？",
                        "すべての行動をコントロールすること",
                        "養育、教育、保護",
                        "経済的支援のみを提供すること",
                        "子どもの代わりに決定すること",
                        "養育、教育、保護"
                },
                {
                        "子どもの親に対する責任は何ですか？",
                        "絶対的服従",
                        "完全な依存",
                        "敬意と孝行",
                        "親の代わりに決定すること",
                        "敬意と孝行"
                },
                {
                        "伝統的な家族は通常どのような特徴を持ちますか？",
                        "世代が少ない",
                        "絶対的に平等な関係",
                        "複数の世代が一緒に住む",
                        "親の役割がない",
                        "複数の世代が一緒に住む"
                },
                {
                        "現代の家族はどのような特徴を持ちますか？",
                        "複数の世代",
                        "押し付けがましい関係",
                        "より平等で民主的",
                        "親戚に依存",
                        "より平等で民主的"
                },
                {
                        "家族の関係はどの原則に基づくべきですか？",
                        "押し付け",
                        "個人的利益",
                        "愛と尊敬",
                        "権力",
                        "愛と尊敬"
                },
                {
                        "家族の葛藤につながる一般的な原因は何ですか？",
                        "コミュニケーションと分かち合いの不足",
                        "狭い住宅",
                        "子どもが多い",
                        "異なる職業",
                        "コミュニケーションと分かち合いの不足"
                },
                {
                        "家族は文化を保護する上でどのような役割を果たしますか？",
                        "文化を発明する",
                        "価値観と伝統を伝える",
                        "社会を管理する",
                        "人々をコントロールする",
                        "価値観と伝統を伝える"
                },
                {
                        "家族はどの要因を通じて個人の人格に影響を与えますか？",
                        "生活環境と教育",
                        "お金",
                        "社会的地位",
                        "権力",
                        "生活環境と教育"
                },
                {
                        "家族メンバーの共通の責任は何ですか？",
                        "自分のことだけを気にする",
                        "分かち合いと互いの助け合い",
                        "他人に依存する",
                        "責任を避ける",
                        "分かち合いと互いの助け合い"
                },
                {
                        "現代社会において家族はどのような役割を果たしますか？",
                        "もはや重要ではない",
                        "個人的な性質のみ",
                        "社会安定の基盤",
                        "経済にのみ奉仕する",
                        "社会安定の基盤"
                },
                {
                        "幸せな家族の表れは何ですか？",
                        "裕福",
                        "葛藤が少ない",
                        "愛と絆",
                        "多くの財産を持つ",
                        "愛と絆"
                },
                {
                        "幸せな家族を築くために最も重要なことは何ですか？",
                        "お金",
                        "権力",
                        "愛と尊敬",
                        "社会的地位",
                        "愛と尊敬"
                }
        };
        return res;
    }

    // Arabic Version - النسخة العربية
    private String[][] familyArabic() {
        String[][] res = {
                {
                        "ما هي الأسرة؟",
                        "مجموعة من الناس يعيشون معاً في مكان واحد",
                        "مجموعة من الناس لديهم علاقات صداقة",
                        "مجموعة من الناس مرتبطين بالزواج أو الدم أو التبني",
                        "منظمة اجتماعية حرة",
                        "مجموعة من الناس مرتبطين بالزواج أو الدم أو التبني"
                },
                {
                        "تتشكل الأسرة بشكل رئيسي على أساس أي علاقة؟",
                        "العلاقة الاقتصادية",
                        "علاقات الدم والزواج والتبني",
                        "العلاقة الاجتماعية",
                        "علاقة الصداقة",
                        "علاقات الدم والزواج والتبني"
                },
                {
                        "لماذا تعتبر الأسرة خلية المجتمع؟",
                        "لأن الأسرة بها الكثير من الناس",
                        "لأن الأسرة موجودة منذ فترة طويلة",
                        "لأن الأسرة هي الأساس لتكوين المجتمع",
                        "لأن الأسرة تدير المجتمع",
                        "لأن الأسرة هي الأساس لتكوين المجتمع"
                },
                {
                        "أي مما يلي ليس وظيفة من وظائف الأسرة؟",
                        "الوظيفة التناسلية",
                        "الوظيفة التعليمية",
                        "الوظيفة الاقتصادية",
                        "الوظيفة العسكرية",
                        "الوظيفة العسكرية"
                },
                {
                        "أين تتجلى الوظيفة التعليمية للأسرة؟",
                        "تعليم المعرفة العلمية",
                        "تكوين الشخصية والأخلاق",
                        "إدارة الدولة",
                        "تطوير التكنولوجيا",
                        "تكوين الشخصية والأخلاق"
                },
                {
                        "ما هو أهم دور للأسرة بالنسبة للأطفال؟",
                        "توفير المال",
                        "التعليم والرعاية",
                        "إدارة الوقت",
                        "السيطرة على السلوك",
                        "التعليم والرعاية"
                },
                {
                        "الأسرة السعيدة هي الأسرة التي تتمتع بأي خصائص؟",
                        "ثرية",
                        "لديها أطفال كثيرون",
                        "الأعضاء يحبون ويحترمون بعضهم البعض",
                        "ذات مكانة اجتماعية عالية",
                        "الأعضاء يحبون ويحترمون بعضهم البعض"
                },
                {
                        "أي عامل يؤثر بشكل مباشر على سعادة الأسرة؟",
                        "الاهتمام والمشاركة",
                        "عدد الأعضاء",
                        "مساحة السكن",
                        "المهنة",
                        "الاهتمام والمشاركة"
                },
                {
                        "ما هي مسؤولية الآباء تجاه أطفالهم؟",
                        "السيطرة على جميع السلوكيات",
                        "التربية والتعليم والحماية",
                        "توفير الدعم المالي فقط",
                        "اتخاذ القرارات نيابة عن الأطفال",
                        "التربية والتعليم والحماية"
                },
                {
                        "ما هي مسؤولية الأطفال تجاه آبائهم؟",
                        "الطاعة المطلقة",
                        "الاعتماد الكامل",
                        "الاحترام وبر الوالدين",
                        "اتخاذ القرارات نيابة عن الآباء",
                        "الاحترام وبر الوالدين"
                },
                {
                        "الأسر التقليدية عادة ما تتمتع بأي خصائص؟",
                        "أجيال قليلة",
                        "علاقات متساوية تماماً",
                        "عدة أجيال تعيش معاً",
                        "لا توجد أدوار أبوية",
                        "عدة أجيال تعيش معاً"
                },
                {
                        "الأسر الحديثة لها أي خصائص؟",
                        "عدة أجيال",
                        "علاقات قسرية",
                        "أكثر مساواة وديمقراطية",
                        "تعتمد على الأقارب",
                        "أكثر مساواة وديمقراطية"
                },
                {
                        "يجب أن تستند العلاقات في الأسرة على أي مبدأ؟",
                        "الإجبار",
                        "المصالح الشخصية",
                        "المحبة والاحترام",
                        "القوة",
                        "المحبة والاحترام"
                },
                {
                        "ما هو السبب الشائع الذي يؤدي إلى الصراعات الأسرية؟",
                        "نقص التواصل والمشاركة",
                        "السكن الضيق",
                        "الأطفال الكثيرون",
                        "المهن المختلفة",
                        "نقص التواصل والمشاركة"
                },
                {
                        "أي دور تلعبه الأسرة في الحفاظ على الثقافة؟",
                        "اختراع الثقافة",
                        "نقل القيم والتقاليد",
                        "إدارة المجتمع",
                        "السيطرة على الناس",
                        "نقل القيم والتقاليد"
                },
                {
                        "تؤثر الأسرة على الشخصية الفردية من خلال أي عامل؟",
                        "بيئة المعيشة والتعليم",
                        "المال",
                        "المكانة الاجتماعية",
                        "القوة",
                        "بيئة المعيشة والتعليم"
                },
                {
                        "ما هي المسؤولية المشتركة لأعضاء الأسرة؟",
                        "الاهتمام بالنفس فقط",
                        "المشاركة ومساعدة بعضهم البعض",
                        "الاعتماد على الآخرين",
                        "تجنب المسؤولية",
                        "المشاركة ومساعدة بعضهم البعض"
                },
                {
                        "أي دور تلعبه الأسرة في المجتمع الحديث؟",
                        "لم تعد مهمة",
                        "ذات طبيعة شخصية فقط",
                        "أساس الاستقرار الاجتماعي",
                        "تخدم الاقتصاد فقط",
                        "أساس الاستقرار الاجتماعي"
                },
                {
                        "ما هو تجلي الأسرة السعيدة؟",
                        "ثرية",
                        "صراعات قليلة",
                        "المحبة والترابط",
                        "امتلاك الكثير من الممتلكات",
                        "المحبة والترابط"
                },
                {
                        "ما هو أهم شيء لبناء أسرة سعيدة؟",
                        "المال",
                        "القوة",
                        "المحبة والاحترام",
                        "المكانة الاجتماعية",
                        "المحبة والاحترام"
                }
        };
        return res;
    }

    // Italian Version - Versione Italiana
    private String[][] familyItalian() {
        String[][] res = {
                {
                        "Cos'è una famiglia?",
                        "Un gruppo di persone che vivono insieme in un posto",
                        "Un gruppo di persone che hanno relazioni di amicizia",
                        "Un insieme di persone unite da matrimonio, legami di sangue o adozione",
                        "Un'organizzazione sociale libera",
                        "Un insieme di persone unite da matrimonio, legami di sangue o adozione"
                },
                {
                        "La famiglia si forma principalmente sulla base di quale relazione?",
                        "Relazione economica",
                        "Legami di sangue, matrimonio, relazione di adozione",
                        "Relazione sociale",
                        "Relazione di amicizia",
                        "Legami di sangue, matrimonio, relazione di adozione"
                },
                {
                        "Perché la famiglia è considerata la cellula della società?",
                        "Perché la famiglia ha molte persone",
                        "Perché la famiglia esiste da molto tempo",
                        "Perché la famiglia è la base per formare la società",
                        "Perché la famiglia gestisce la società",
                        "Perché la famiglia è la base per formare la società"
                },
                {
                        "Quale delle seguenti NON è una funzione della famiglia?",
                        "Funzione riproduttiva",
                        "Funzione educativa",
                        "Funzione economica",
                        "Funzione militare",
                        "Funzione militare"
                },
                {
                        "Dove si manifesta la funzione educativa della famiglia?",
                        "Insegnare conoscenza scientifica",
                        "Formare personalità e moralità",
                        "Gestire lo stato",
                        "Sviluppare tecnologia",
                        "Formare personalità e moralità"
                },
                {
                        "Qual è il ruolo più importante della famiglia per i bambini?",
                        "Fornire denaro",
                        "Educazione e cura",
                        "Gestire il tempo",
                        "Controllare il comportamento",
                        "Educazione e cura"
                },
                {
                        "Una famiglia felice è una famiglia con quali caratteristiche?",
                        "Ricca",
                        "Molti bambini",
                        "I membri si amano e si rispettano a vicenda",
                        "Alto status sociale",
                        "I membri si amano e si rispettano a vicenda"
                },
                {
                        "Quale fattore influisce direttamente sulla felicità familiare?",
                        "Cura e condivisione",
                        "Numero di membri",
                        "Area abitativa",
                        "Occupazione",
                        "Cura e condivisione"
                },
                {
                        "Qual è la responsabilità dei genitori verso i loro figli?",
                        "Controllare tutti i comportamenti",
                        "Nutrire, educare e proteggere",
                        "Fornire solo supporto finanziario",
                        "Prendere decisioni per i figli",
                        "Nutrire, educare e proteggere"
                },
                {
                        "Qual è la responsabilità dei figli verso i loro genitori?",
                        "Obbedienza assoluta",
                        "Dipendenza completa",
                        "Rispetto e pietà filiale",
                        "Prendere decisioni per i genitori",
                        "Rispetto e pietà filiale"
                },
                {
                        "Le famiglie tradizionali di solito hanno quali caratteristiche?",
                        "Poche generazioni",
                        "Relazioni assolutamente uguali",
                        "Più generazioni che vivono insieme",
                        "Nessun ruolo genitoriale",
                        "Più generazioni che vivono insieme"
                },
                {
                        "Le famiglie moderne hanno quali caratteristiche?",
                        "Più generazioni",
                        "Relazioni imposte",
                        "Più uguali e democratiche",
                        "Dipendenti dai parenti",
                        "Più uguali e democratiche"
                },
                {
                        "Le relazioni in famiglia dovrebbero essere basate su quale principio?",
                        "Imposizione",
                        "Interessi personali",
                        "Amore e rispetto",
                        "Potere",
                        "Amore e rispetto"
                },
                {
                        "Qual è la causa comune che porta a conflitti familiari?",
                        "Mancanza di comunicazione e condivisione",
                        "Alloggio ristretto",
                        "Molti bambini",
                        "Occupazioni diverse",
                        "Mancanza di comunicazione e condivisione"
                },
                {
                        "Che ruolo gioca la famiglia nel preservare la cultura?",
                        "Inventare cultura",
                        "Trasmettere valori e tradizioni",
                        "Gestire la società",
                        "Controllare le persone",
                        "Trasmettere valori e tradizioni"
                },
                {
                        "La famiglia influenza la personalità individuale attraverso quale fattore?",
                        "Ambiente di vita e educazione",
                        "Denaro",
                        "Status sociale",
                        "Potere",
                        "Ambiente di vita e educazione"
                },
                {
                        "Qual è la responsabilità comune dei membri della famiglia?",
                        "Prendersi cura solo di se stessi",
                        "Condividere e aiutarsi a vicenda",
                        "Dipendere dagli altri",
                        "Evitare responsabilità",
                        "Condividere e aiutarsi a vicenda"
                },
                {
                        "Che ruolo gioca la famiglia nella società moderna?",
                        "Non è più importante",
                        "Solo di natura personale",
                        "Base per la stabilità sociale",
                        "Serve solo l'economia",
                        "Base per la stabilità sociale"
                },
                {
                        "Qual è la manifestazione di una famiglia felice?",
                        "Ricca",
                        "Pochi conflitti",
                        "Amore e legame",
                        "Avere molti beni",
                        "Amore e legame"
                },
                {
                        "Qual è la cosa più importante per costruire una famiglia felice?",
                        "Denaro",
                        "Potere",
                        "Amore e rispetto",
                        "Status sociale",
                        "Amore e rispetto"
                }
        };
        return res;
    }

    // Turkish Version - Türkçe Versiyon
    private String[][] familyTurkish() {
        String[][] res = {
                {
                        "Aile nedir?",
                        "Bir yerde birlikte yaşayan insanların grubu",
                        "Arkadaşlık ilişkisi olan insanların grubu",
                        "Evlilik, kan bağı veya evlat edinme ile bağlı insanların topluluğu",
                        "Özgür toplumsal organizasyon",
                        "Evlilik, kan bağı veya evlat edinme ile bağlı insanların topluluğu"
                },
                {
                        "Aile esas olarak hangi ilişkiye dayalı oluşur?",
                        "Ekonomik ilişki",
                        "Kan bağı, evlilik, evlat edinme ilişkisi",
                        "Toplumsal ilişki",
                        "Arkadaşlık ilişkisi",
                        "Kan bağı, evlilik, evlat edinme ilişkisi"
                },
                {
                        "Aile neden toplumun hücresi olarak kabul edilir?",
                        "Çünkü ailede çok insan var",
                        "Çünkü aile uzun zamandır var",
                        "Çünkü aile toplumu oluşturmak için temeldir",
                        "Çünkü aile toplumu yönetir",
                        "Çünkü aile toplumu oluşturmak için temeldir"
                },
                {
                        "Aşağıdakilerden hangisi ailenin işlevi değildir?",
                        "Üreme işlevi",
                        "Eğitim işlevi",
                        "Ekonomik işlev",
                        "Askeri işlev",
                        "Askeri işlev"
                },
                {
                        "Ailenin eğitim işlevi nerede kendini gösterir?",
                        "Bilimsel bilgi öğretmek",
                        "Kişilik ve ahlak oluşturmak",
                        "Devleti yönetmek",
                        "Teknoloji geliştirmek",
                        "Kişilik ve ahlak oluşturmak"
                },
                {
                        "Çocuklar için ailenin en önemli rolü nedir?",
                        "Para sağlamak",
                        "Eğitim ve bakım",
                        "Zamanı yönetmek",
                        "Davranışı kontrol etmek",
                        "Eğitim ve bakım"
                },
                {
                        "Mutlu aile hangi özelliklere sahip ailedir?",
                        "Zengin",
                        "Çok çocuklu",
                        "Üyeler birbirlerini sever ve sayar",
                        "Yüksek sosyal statülü",
                        "Üyeler birbirlerini sever ve sayar"
                },
                {
                        "Aile mutluluğunu doğrudan etkileyen faktör nedir?",
                        "İlgi ve paylaşım",
                        "Üye sayısı",
                        "Konut alanı",
                        "Meslek",
                        "İlgi ve paylaşım"
                },
                {
                        "Ebeveynlerin çocuklarına karşı sorumluluğu nedir?",
                        "Tüm davranışları kontrol etmek",
                        "Beslemek, eğitmek ve korumak",
                        "Sadece mali destek sağlamak",
                        "Çocuklar adına karar vermek",
                        "Beslemek, eğitmek ve korumak"
                },
                {
                        "Çocukların ebeveynlerine karşı sorumluluğu nedir?",
                        "Mutlak itaat",
                        "Tam bağımlılık",
                        "Saygı ve vefa",
                        "Ebeveynler adına karar vermek",
                        "Saygı ve vefa"
                },
                {
                        "Geleneksel aileler genellikle hangi özelliklere sahiptir?",
                        "Az nesil",
                        "Kesinlikle eşit ilişkiler",
                        "Birden fazla nesil birlikte yaşar",
                        "Ebeveyn rolleri yok",
                        "Birden fazla nesil birlikte yaşar"
                },
                {
                        "Modern aileler hangi özelliklere sahiptir?",
                        "Birden fazla nesil",
                        "Zorla dayatan ilişkiler",
                        "Daha eşit ve demokratik",
                        "Akrabalara bağımlı",
                        "Daha eşit ve demokratik"
                },
                {
                        "Ailede ilişkiler hangi ilkeye dayanmalıdır?",
                        "Zorla dayatma",
                        "Kişisel çıkarlar",
                        "Sevgi ve saygı",
                        "Güç",
                        "Sevgi ve saygı"
                },
                {
                        "Aile çatışmalarına yol açan yaygın neden nedir?",
                        "İletişim ve paylaşım eksikliği",
                        "Dar konut",
                        "Çok çocuk",
                        "Farklı meslekler",
                        "İletişim ve paylaşım eksikliği"
                },
                {
                        "Aile kültürü korumada hangi rolü oynar?",
                        "Kültür icat etmek",
                        "Değerleri ve gelenekleri aktarmak",
                        "Toplumu yönetmek",
                        "İnsanları kontrol etmek",
                        "Değerleri ve gelenekleri aktarmak"
                },
                {
                        "Aile bireysel kişiliği hangi faktör aracılığıyla etkiler?",
                        "Yaşam çevresi ve eğitim",
                        "Para",
                        "Sosyal statü",
                        "Güç",
                        "Yaşam çevresi ve eğitim"
                },
                {
                        "Aile üyelerinin ortak sorumluluğu nedir?",
                        "Sadece kendini düşünmek",
                        "Paylaşmak ve birbirine yardım etmek",
                        "Başkalarına bağımlı olmak",
                        "Sorumluluktan kaçınmak",
                        "Paylaşmak ve birbirine yardım etmek"
                },
                {
                        "Aile modern toplumda hangi rolü oynar?",
                        "Artık önemli değil",
                        "Sadece kişisel nitelikte",
                        "Toplumsal istikrarın temeli",
                        "Sadece ekonomiye hizmet eder",
                        "Toplumsal istikrarın temeli"
                },
                {
                        "Mutlu ailenin göstergesi nedir?",
                        "Zengin",
                        "Az çatışma",
                        "Sevgi ve bağ",
                        "Çok mal varlığına sahip",
                        "Sevgi ve bağ"
                },
                {
                        "Mutlu aile kurmak için en önemli şey nedir?",
                        "Para",
                        "Güç",
                        "Sevgi ve saygı",
                        "Sosyal statü",
                        "Sevgi ve saygı"
                }
        };
        return res;
    }

    // Dutch Version - Nederlandse Versie
    private String[][] familyDutch() {
        String[][] res = {
                {
                        "Wat is een familie?",
                        "Een groep mensen die samen op één plaats wonen",
                        "Een groep mensen die vriendschapsrelaties hebben",
                        "Een verzameling mensen verbonden door huwelijk, bloedverwantschap of adoptie",
                        "Een vrije sociale organisatie",
                        "Een verzameling mensen verbonden door huwelijk, bloedverwantschap of adoptie"
                },
                {
                        "Familie wordt voornamelijk gevormd op basis van welke relatie?",
                        "Economische relatie",
                        "Bloedverwantschap, huwelijk, adoptierelatie",
                        "Sociale relatie",
                        "Vriendschapsrelatie",
                        "Bloedverwantschap, huwelijk, adoptierelatie"
                },
                {
                        "Waarom wordt familie beschouwd als de cel van de samenleving?",
                        "Omdat familie veel mensen heeft",
                        "Omdat familie al lang bestaat",
                        "Omdat familie de basis is voor het vormen van de samenleving",
                        "Omdat familie de samenleving bestuurt",
                        "Omdat familie de basis is voor het vormen van de samenleving"
                },
                {
                        "Welke van de volgende is GEEN functie van de familie?",
                        "Reproductieve functie",
                        "Educatieve functie",
                        "Economische functie",
                        "Militaire functie",
                        "Militaire functie"
                },
                {
                        "Waar manifesteert zich de educatieve functie van de familie?",
                        "Wetenschappelijke kennis onderwijzen",
                        "Persoonlijkheid en moraliteit vormen",
                        "De staat besturen",
                        "Technologie ontwikkelen",
                        "Persoonlijkheid en moraliteit vormen"
                },
                {
                        "Wat is de belangrijkste rol van de familie voor kinderen?",
                        "Geld verstrekken",
                        "Onderwijs en zorg",
                        "Tijd beheren",
                        "Gedrag controleren",
                        "Onderwijs en zorg"
                },
                {
                        "Een gelukkige familie is een familie met welke kenmerken?",
                        "Rijk",
                        "Veel kinderen",
                        "Leden houden van elkaar en respecteren elkaar",
                        "Hoge sociale status",
                        "Leden houden van elkaar en respecteren elkaar"
                },
                {
                        "Welke factor beïnvloedt het familiegeluk direct?",
                        "Zorg en delen",
                        "Aantal leden",
                        "Woonruimte",
                        "Beroep",
                        "Zorg en delen"
                },
                {
                        "Wat is de verantwoordelijkheid van ouders tegenover hun kinderen?",
                        "Alle gedragingen controleren",
                        "Voeden, onderwijzen en beschermen",
                        "Alleen financiële ondersteuning bieden",
                        "Beslissingen nemen voor kinderen",
                        "Voeden, onderwijzen en beschermen"
                },
                {
                        "Wat is de verantwoordelijkheid van kinderen tegenover hun ouders?",
                        "Absolute gehoorzaamheid",
                        "Volledige afhankelijkheid",
                        "Respect en kinderlijke liefde",
                        "Beslissingen nemen voor ouders",
                        "Respect en kinderlijke liefde"
                },
                {
                        "Traditionele families hebben meestal welke kenmerken?",
                        "Weinig generaties",
                        "Absoluut gelijke relaties",
                        "Meerdere generaties wonen samen",
                        "Geen ouderlijke rollen",
                        "Meerdere generaties wonen samen"
                },
                {
                        "Moderne families hebben welke kenmerken?",
                        "Meerdere generaties",
                        "Opdringende relaties",
                        "Meer gelijk en democratisch",
                        "Afhankelijk van familieleden",
                        "Meer gelijk en democratisch"
                },
                {
                        "Relaties in de familie moeten gebaseerd zijn op welk principe?",
                        "Opdringen",
                        "Persoonlijke belangen",
                        "Liefde en respect",
                        "Macht",
                        "Liefde en respect"
                },
                {
                        "Wat is de veelvoorkomende oorzaak die leidt tot familieconflicten?",
                        "Gebrek aan communicatie en delen",
                        "Krappe behuizing",
                        "Veel kinderen",
                        "Verschillende beroepen",
                        "Gebrek aan communicatie en delen"
                },
                {
                        "Welke rol speelt familie bij het behouden van cultuur?",
                        "Cultuur uitvinden",
                        "Waarden en tradities doorgeven",
                        "Samenleving besturen",
                        "Mensen controleren",
                        "Waarden en tradities doorgeven"
                },
                {
                        "Familie beïnvloedt individuele persoonlijkheid door welke factor?",
                        "Leefomgeving en onderwijs",
                        "Geld",
                        "Sociale status",
                        "Macht",
                        "Leefomgeving en onderwijs"
                },
                {
                        "Wat is de gemeenschappelijke verantwoordelijkheid van familieleden?",
                        "Alleen voor jezelf zorgen",
                        "Delen en elkaar helpen",
                        "Afhangen van anderen",
                        "Verantwoordelijkheid vermijden",
                        "Delen en elkaar helpen"
                },
                {
                        "Welke rol speelt familie in de moderne samenleving?",
                        "Niet meer belangrijk",
                        "Alleen van persoonlijke aard",
                        "Basis voor sociale stabiliteit",
                        "Dient alleen de economie",
                        "Basis voor sociale stabiliteit"
                },
                {
                        "Wat is de manifestatie van een gelukkige familie?",
                        "Rijk",
                        "Weinig conflicten",
                        "Liefde en verbinding",
                        "Veel bezittingen hebben",
                        "Liefde en verbinding"
                },
                {
                        "Wat is het belangrijkste om een gelukkige familie op te bouwen?",
                        "Geld",
                        "Macht",
                        "Liefde en respect",
                        "Sociale status",
                        "Liefde en respect"
                }
        };
        return res;
    }

    // Thai Version - เวอร์ชันไทย
    private String[][] familyThai() {
        String[][] res = {
                {
                        "ครอบครัวคืออะไร?",
                        "กลุ่มคนที่อาศัยอยู่ร่วมกันในที่เดียว",
                        "กลุ่มคนที่มีความสัมพันธ์แบบเพื่อน",
                        "กลุ่มคนที่เชื่อมโยงกันด้วยการแต่งงาน สายเลือด หรือการรับเลี้ยงบุตรบุญธรรม",
                        "องค์กรทางสังคมที่เสรี",
                        "กลุ่มคนที่เชื่อมโยงกันด้วยการแต่งงาน สายเลือด หรือการรับเลี้ยงบุตรบุญธรรม"
                },
                {
                        "ครอบครัวเกิดขึ้นโดยหลักแล้วอิงจากความสัมพันธ์แบบไหน?",
                        "ความสัมพันธ์ทางเศรษฐกิจ",
                        "ความสัมพันธ์ทางสายเลือด การแต่งงาน การรับเลี้ยงบุตรบุญธรรม",
                        "ความสัมพันธ์ทางสังคม",
                        "ความสัมพันธ์แบบเพื่อน",
                        "ความสัมพันธ์ทางสายเลือด การแต่งงาน การรับเลี้ยงบุตรบุญธรรม"
                },
                {
                        "ทำไมครอบครัวถึงถูกมองว่าเป็นหน่วยพื้นฐานของสังคม?",
                        "เพราะครอบครัวมีคนมาก",
                        "เพราะครอบครัวมีมานาน",
                        "เพราะครอบครัวเป็นรากฐานสำหรับการสร้างสังคม",
                        "เพราะครอบครัวจัดการสังคม",
                        "เพราะครอบครัวเป็นรากฐานสำหรับการสร้างสังคม"
                },
                {
                        "ข้อใดต่อไปนี้ไม่ใช่หน้าที่ของครอบครัว?",
                        "หน้าที่การสืบพันธุ์",
                        "หน้าที่การศึกษา",
                        "หน้าที่เศรษฐกิจ",
                        "หน้าที่ทหาร",
                        "หน้าที่ทหาร"
                },
                {
                        "หน้าที่การศึกษาของครอบครัวแสดงออกที่ไหน?",
                        "การสอนความรู้ทางวิทยาศาสตร์",
                        "การสร้างบุคลิกภาพและศีลธรรม",
                        "การจัดการรัฐ",
                        "การพัฒนาเทคโนโลยี",
                        "การสร้างบุคลิกภาพและศีลธรรม"
                },
                {
                        "บทบาทที่สำคัญที่สุดของครอบครัวต่อเด็กคืออะไร?",
                        "การให้เงิน",
                        "การศึกษาและการดูแล",
                        "การจัดการเวลา",
                        "การควบคุมพฤติกรรม",
                        "การศึกษาและการดูแล"
                },
                {
                        "ครอบครัวที่มีความสุขคือครอบครัวที่มีลักษณะอย่างไร?",
                        "มั่งคั่ง",
                        "มีลูกมาก",
                        "สมาชิกรักและเคารพซึ่งกันและกัน",
                        "มีสถานะทางสังคมสูง",
                        "สมาชิกรักและเคารพซึ่งกันและกัน"
                },
                {
                        "ปัจจัยใดที่ส่งผลโดยตรงต่อความสุขของครอบครัว?",
                        "การห่วงใยและการแบ่งปัน",
                        "จำนวนสมาชิก",
                        "พื้นที่ที่อยู่อาศัย",
                        "อาชีพ",
                        "การห่วงใยและการแบ่งปัน"
                },
                {
                        "ความรับผิดชอบของพ่อแม่ต่อลูกคืออะไร?",
                        "ควบคุมพฤติกรรมทั้งหมด",
                        "เลี้ยงดู ให้การศึกษา และปกป้อง",
                        "ให้การสนับสนุนทางการเงินเท่านั้น",
                        "ตัดสินใจแทนลูก",
                        "เลี้ยงดู ให้การศึกษา และปกป้อง"
                },
                {
                        "ความรับผิดชอบของลูกต่อพ่อแม่คืออะไร?",
                        "เชื่อฟังอย่างสมบูรณ์",
                        "พึ่งพาอย่างสมบูรณ์",
                        "เคารพและกตัญญูกตเวทิตา",
                        "ตัดสินใจแทนพ่อแม่",
                        "เคารพและกตัญญูกตเวทิตา"
                },
                {
                        "ครอบครัวแบบดั้งเดิมมักมีลักษณะอย่างไร?",
                        "รุ่นน้อย",
                        "ความสัมพันธ์ที่เท่าเทียมกันอย่างสมบูรณ์",
                        "หลายรุ่นอาศัยอยู่ด้วยกัน",
                        "ไม่มีบทบาทของพ่อแม่",
                        "หลายรุ่นอาศัยอยู่ด้วยกัน"
                },
                {
                        "ครอบครัวสมัยใหม่มีลักษณะอย่างไร?",
                        "หลายรุ่น",
                        "ความสัมพันธ์แบบบังคับ",
                        "เท่าเทียมและเป็นประชาธิปไตยมากขึ้น",
                        "พึ่งพาญาติ",
                        "เท่าเทียมและเป็นประชาธิปไตยมากขึ้น"
                },
                {
                        "ความสัมพันธ์ในครอบครัวควรอิงตามหลักการใด?",
                        "การบังคับ",
                        "ผลประโยชน์ส่วนตัว",
                        "ความรักและความเคารพ",
                        "อำนาจ",
                        "ความรักและความเคารพ"
                },
                {
                        "สาเหตุทั่วไปที่นำไปสู่ความขัดแย้งในครอบครัวคืออะไร?",
                        "ขาดการสื่อสารและการแบ่งปัน",
                        "ที่อยู่อาศัยแคบ",
                        "ลูกเยอะ",
                        "อาชีพต่างกัน",
                        "ขาดการสื่อสารและการแบ่งปัน"
                },
                {
                        "ครอบครัวมีบทบาทอย่างไรในการรักษาวัฒนธรรม?",
                        "คิดค้นวัฒนธรรม",
                        "ถ่ายทอดค่านิยมและประเพณี",
                        "จัดการสังคม",
                        "ควบคุมคน",
                        "ถ่ายทอดค่านิยมและประเพณี"
                },
                {
                        "ครอบครัวมีอิทธิพลต่อบุคลิกภาพของบุคคลผ่านปัจจัยใด?",
                        "สภาพแวดล้อมการใช้ชีวิตและการศึกษา",
                        "เงิน",
                        "สถานะทางสังคม",
                        "อำนาจ",
                        "สภาพแวดล้อมการใช้ชีวิตและการศึกษา"
                },
                {
                        "ความรับผิดชอบร่วมของสมาชิกครอบครัวคืออะไร?",
                        "ดูแลเฉพาะตัวเอง",
                        "แบ่งปันและช่วยเหลือซึ่งกันและกัน",
                        "พึ่งพาผู้อื่น",
                        "หลีกเลี่ยงความรับผิดชอบ",
                        "แบ่งปันและช่วยเหลือซึ่งกันและกัน"
                },
                {
                        "ครอบครัวมีบทบาทอย่างไรในสังคมสมัยใหม่?",
                        "ไม่สำคัญอีกต่อไป",
                        "เป็นเรื่องส่วนตัวเท่านั้น",
                        "รากฐานความมั่นคงทางสังคม",
                        "รับใช้เศรษฐกิจเท่านั้น",
                        "รากฐานความมั่นคงทางสังคม"
                },
                {
                        "การแสดงออกของครอบครัวที่มีความสุขคืออะไร?",
                        "มั่งคั่ง",
                        "ขัดแย้งน้อย",
                        "ความรักและความผูกพัน",
                        "มีทรัพย์สินมาก",
                        "ความรักและความผูกพัน"
                },
                {
                        "สิ่งที่สำคัญที่สุดในการสร้างครอบครัวที่มีความสุขคืออะไร?",
                        "เงิน",
                        "อำนาจ",
                        "ความรักและความเคารพ",
                        "สถานะทางสังคม",
                        "ความรักและความเคารพ"
                }
        };
        return res;
    }

    // Master function to get family questions by language ID
    private String[][] getFamilyQuestionsByLanguage(Long languageId) {
        switch (languageId.intValue()) {
            case 1: return familyEnglish();
            case 2: return familyVN();
            case 3: return familySpanish();
            case 4: return familyFrench();
            case 5: return familyGerman();
            case 6: return familyChinese();
            case 7: return familyJapanese();
            case 8: return familyKorean();
            case 9: return familyHindi();
            case 10: return familyRussian();
            case 11: return familyPortuguese();
            case 12: return familyArabic();
            case 13: return familyItalian();
            case 14: return familyTurkish();
            case 15: return familyDutch();
            case 16: return familyThai();
            default: return familyVN(); // Default to Vietnamese
        }
    }
    // Hàm trả về câu hỏi tiếng Anh
    public  String[][] workEnglish() {
        return new String[][] {
                {
                        "What is work?",
                        "Personal recreational activity",
                        "Labor activity aimed at generating income or value",
                        "Learning activity",
                        "Resting activity",
                        "Labor activity aimed at generating income or value"
                },
                {
                        "What is the main purpose of work?",
                        "Entertainment",
                        "Generate income and stabilize life",
                        "Kill time",
                        "Avoid unemployment",
                        "Generate income and stabilize life"
                },
                {
                        "Which of the following factors is most important for effective work?",
                        "Luck",
                        "Skills and knowledge",
                        "Age",
                        "Geographic location",
                        "Skills and knowledge"
                },
                {
                        "What is labor discipline?",
                        "Working freely without rules",
                        "Compliance with regulations, time and responsibilities at work",
                        "Only work according to preferences",
                        "Work when feeling like it",
                        "Compliance with regulations, time and responsibilities at work"
                },
                {
                        "Where is the spirit of responsibility at work shown?",
                        "Working when supervised",
                        "Complete assigned tasks on time and with quality",
                        "Only do easy work",
                        "Shirk responsibility",
                        "Complete assigned tasks on time and with quality"
                },
                {
                        "What are the benefits of teamwork?",
                        "Reduce individual responsibility",
                        "Increase work efficiency and quality",
                        "Easy to skip work",
                        "No need to communicate",
                        "Increase work efficiency and quality"
                },
                {
                        "What does communication skills at work help?",
                        "Create conflicts",
                        "Misunderstanding",
                        "Coordinate work effectively",
                        "Work slower",
                        "Coordinate work effectively"
                },
                {
                        "What is professional ethics?",
                        "Rules set by individuals",
                        "Behavioral standards to be followed at work",
                        "Traffic laws",
                        "Family regulations",
                        "Behavioral standards to be followed at work"
                },
                {
                        "Positive work attitude is shown through what?",
                        "Avoiding tasks",
                        "Proactive, cooperative and responsible",
                        "Working perfunctorily",
                        "Only following orders",
                        "Proactive, cooperative and responsible"
                },
                {
                        "What is labor productivity?",
                        "Working hours",
                        "Efficiency achieved in the labor process",
                        "Salary received",
                        "Position in company",
                        "Efficiency achieved in the labor process"
                },
                {
                        "Career choice should be based on which factor?",
                        "Interests, abilities and social needs",
                        "Friends' opinions",
                        "Trending careers",
                        "Geographic distance",
                        "Interests, abilities and social needs"
                },
                {
                        "What are soft skills at work?",
                        "Professional skills",
                        "Communication, teamwork, time management skills",
                        "Machine skills",
                        "Manual skills",
                        "Communication, teamwork, time management skills"
                },
                {
                        "What is the meaning of learning spirit at work?",
                        "Not necessary",
                        "Help improve qualifications and adapt to changes",
                        "Slow down work",
                        "Waste time",
                        "Help improve qualifications and adapt to changes"
                },
                {
                        "How does work environment affect workers?",
                        "No effect",
                        "Affects work performance and spirit",
                        "Only affects salary",
                        "Only affects working hours",
                        "Affects work performance and spirit"
                },
                {
                        "What are workers' rights?",
                        "Only work without rest",
                        "Guaranteed rights and labor safety",
                        "No complaints allowed",
                        "No leave allowed",
                        "Guaranteed rights and labor safety"
                },
                {
                        "What are workers' obligations?",
                        "Work arbitrarily",
                        "Comply with contracts and labor regulations",
                        "Only do what you like",
                        "No responsibility needed",
                        "Comply with contracts and labor regulations"
                },
                {
                        "What does work-life balance mean?",
                        "Work more",
                        "Help maintain health and long-term effectiveness",
                        "Reduce income",
                        "Work less",
                        "Help maintain health and long-term effectiveness"
                },
                {
                        "What is unemployment?",
                        "Don't want to work",
                        "No job but need to work",
                        "Long-term leave",
                        "Freelance work",
                        "No job but need to work"
                },
                {
                        "What is the purpose of professional training and development?",
                        "Increase working hours",
                        "Improve skills and professional qualifications",
                        "Reduce responsibility",
                        "Entertainment",
                        "Improve skills and professional qualifications"
                },
                {
                        "In your opinion, what is the most important factor for success at work?",
                        "Luck",
                        "Relationships",
                        "Effort and responsibility",
                        "Status",
                        "Effort and responsibility"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Việt (dữ liệu gốc)
    public  String[][] workVN() {
        return new String[][] {
                {
                        "Công việc là gì?",
                        "Hoạt động giải trí cá nhân",
                        "Hoạt động lao động nhằm tạo ra thu nhập hoặc giá trị",
                        "Hoạt động học tập",
                        "Hoạt động nghỉ ngơi",
                        "Hoạt động lao động nhằm tạo ra thu nhập hoặc giá trị"
                },
                {
                        "Mục đích chính của công việc là gì?",
                        "Giải trí",
                        "Tạo thu nhập và ổn định cuộc sống",
                        "Tiêu thời gian",
                        "Tránh thất nghiệp",
                        "Tạo thu nhập và ổn định cuộc sống"
                },
                {
                        "Yếu tố nào sau đây quan trọng nhất để làm việc hiệu quả?",
                        "May mắn",
                        "Kỹ năng và kiến thức",
                        "Tuổi tác",
                        "Vị trí địa lý",
                        "Kỹ năng và kiến thức"
                },
                {
                        "Kỷ luật lao động là gì?",
                        "Làm việc tự do không quy tắc",
                        "Tuân thủ nội quy, thời gian và trách nhiệm trong công việc",
                        "Chỉ làm theo sở thích",
                        "Làm việc khi có hứng",
                        "Tuân thủ nội quy, thời gian và trách nhiệm trong công việc"
                },
                {
                        "Tinh thần trách nhiệm trong công việc thể hiện ở đâu?",
                        "Làm việc khi có người giám sát",
                        "Hoàn thành nhiệm vụ được giao đúng hạn và chất lượng",
                        "Chỉ làm việc dễ",
                        "Đùn đẩy trách nhiệm",
                        "Hoàn thành nhiệm vụ được giao đúng hạn và chất lượng"
                },
                {
                        "Làm việc nhóm có lợi ích gì?",
                        "Giảm trách nhiệm cá nhân",
                        "Tăng hiệu quả và chất lượng công việc",
                        "Dễ trốn việc",
                        "Không cần giao tiếp",
                        "Tăng hiệu quả và chất lượng công việc"
                },
                {
                        "Kỹ năng giao tiếp trong công việc giúp điều gì?",
                        "Tạo mâu thuẫn",
                        "Hiểu lầm",
                        "Phối hợp công việc hiệu quả",
                        "Làm việc chậm hơn",
                        "Phối hợp công việc hiệu quả"
                },
                {
                        "Đạo đức nghề nghiệp là gì?",
                        "Quy tắc do cá nhân đặt ra",
                        "Chuẩn mực hành vi cần tuân thủ trong công việc",
                        "Luật giao thông",
                        "Quy định gia đình",
                        "Chuẩn mực hành vi cần tuân thủ trong công việc"
                },
                {
                        "Thái độ làm việc tích cực thể hiện qua điều nào?",
                        "Trốn tránh nhiệm vụ",
                        "Chủ động, hợp tác và có trách nhiệm",
                        "Làm việc đối phó",
                        "Chỉ làm theo lệnh",
                        "Chủ động, hợp tác và có trách nhiệm"
                },
                {
                        "Năng suất lao động là gì?",
                        "Số giờ làm việc",
                        "Hiệu quả đạt được trong quá trình lao động",
                        "Mức lương nhận được",
                        "Chức vụ trong công ty",
                        "Hiệu quả đạt được trong quá trình lao động"
                },
                {
                        "Việc lựa chọn nghề nghiệp cần dựa trên yếu tố nào?",
                        "Sở thích, năng lực và nhu cầu xã hội",
                        "Ý kiến bạn bè",
                        "Nghề đang hot",
                        "Khoảng cách địa lý",
                        "Sở thích, năng lực và nhu cầu xã hội"
                },
                {
                        "Kỹ năng mềm trong công việc là gì?",
                        "Kỹ năng chuyên môn",
                        "Kỹ năng giao tiếp, làm việc nhóm, quản lý thời gian",
                        "Kỹ năng máy móc",
                        "Kỹ năng tay nghề",
                        "Kỹ năng giao tiếp, làm việc nhóm, quản lý thời gian"
                },
                {
                        "Tinh thần học hỏi trong công việc có ý nghĩa gì?",
                        "Không cần thiết",
                        "Giúp nâng cao trình độ và thích nghi với thay đổi",
                        "Làm việc chậm lại",
                        "Tốn thời gian",
                        "Giúp nâng cao trình độ và thích nghi với thay đổi"
                },
                {
                        "Môi trường làm việc ảnh hưởng như thế nào đến người lao động?",
                        "Không ảnh hưởng",
                        "Ảnh hưởng đến hiệu suất và tinh thần làm việc",
                        "Chỉ ảnh hưởng đến lương",
                        "Chỉ ảnh hưởng đến giờ làm",
                        "Ảnh hưởng đến hiệu suất và tinh thần làm việc"
                },
                {
                        "Quyền của người lao động là gì?",
                        "Chỉ làm việc không được nghỉ",
                        "Được bảo đảm quyền lợi và an toàn lao động",
                        "Không được khiếu nại",
                        "Không được nghỉ phép",
                        "Được bảo đảm quyền lợi và an toàn lao động"
                },
                {
                        "Nghĩa vụ của người lao động là gì?",
                        "Làm việc tùy ý",
                        "Tuân thủ hợp đồng và nội quy lao động",
                        "Chỉ làm việc mình thích",
                        "Không cần trách nhiệm",
                        "Tuân thủ hợp đồng và nội quy lao động"
                },
                {
                        "Cân bằng giữa công việc và cuộc sống có ý nghĩa gì?",
                        "Làm việc nhiều hơn",
                        "Giúp duy trì sức khỏe và hiệu quả lâu dài",
                        "Giảm thu nhập",
                        "Làm việc ít lại",
                        "Giúp duy trì sức khỏe và hiệu quả lâu dài"
                },
                {
                        "Thất nghiệp là gì?",
                        "Không muốn làm việc",
                        "Không có việc làm nhưng có nhu cầu làm việc",
                        "Nghỉ phép dài hạn",
                        "Làm việc tự do",
                        "Không có việc làm nhưng có nhu cầu làm việc"
                },
                {
                        "Đào tạo và bồi dưỡng nghề nghiệp nhằm mục đích gì?",
                        "Tăng giờ làm",
                        "Nâng cao kỹ năng và trình độ chuyên môn",
                        "Giảm trách nhiệm",
                        "Giải trí",
                        "Nâng cao kỹ năng và trình độ chuyên môn"
                },
                {
                        "Theo bạn, yếu tố quan trọng nhất để thành công trong công việc là gì?",
                        "May mắn",
                        "Quan hệ",
                        "Nỗ lực và trách nhiệm",
                        "Địa vị",
                        "Nỗ lực và trách nhiệm"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Tây Ban Nha
    public  String[][] workSpanish() {
        return new String[][] {
                {
                        "¿Qué es el trabajo?",
                        "Actividad recreativa personal",
                        "Actividad laboral destinada a generar ingresos o valor",
                        "Actividad de aprendizaje",
                        "Actividad de descanso",
                        "Actividad laboral destinada a generar ingresos o valor"
                },
                {
                        "¿Cuál es el propósito principal del trabajo?",
                        "Entretenimiento",
                        "Generar ingresos y estabilizar la vida",
                        "Matar el tiempo",
                        "Evitar el desempleo",
                        "Generar ingresos y estabilizar la vida"
                },
                {
                        "¿Cuál de los siguientes factores es más importante para trabajar eficazmente?",
                        "Suerte",
                        "Habilidades y conocimientos",
                        "Edad",
                        "Ubicación geográfica",
                        "Habilidades y conocimientos"
                },
                {
                        "¿Qué es la disciplina laboral?",
                        "Trabajar libremente sin reglas",
                        "Cumplir con regulaciones, tiempo y responsabilidades en el trabajo",
                        "Solo trabajar según preferencias",
                        "Trabajar cuando se tenga ganas",
                        "Cumplir con regulaciones, tiempo y responsabilidades en el trabajo"
                },
                {
                        "¿Dónde se muestra el espíritu de responsabilidad en el trabajo?",
                        "Trabajar cuando se está supervisado",
                        "Completar tareas asignadas a tiempo y con calidad",
                        "Solo hacer trabajo fácil",
                        "Eludir responsabilidades",
                        "Completar tareas asignadas a tiempo y con calidad"
                },
                {
                        "¿Cuáles son los beneficios del trabajo en equipo?",
                        "Reducir la responsabilidad individual",
                        "Aumentar la eficiencia y calidad del trabajo",
                        "Fácil faltar al trabajo",
                        "No necesidad de comunicarse",
                        "Aumentar la eficiencia y calidad del trabajo"
                },
                {
                        "¿En qué ayudan las habilidades de comunicación en el trabajo?",
                        "Crear conflictos",
                        "Malentendidos",
                        "Coordinar el trabajo eficazmente",
                        "Trabajar más lento",
                        "Coordinar el trabajo eficazmente"
                },
                {
                        "¿Qué es la ética profesional?",
                        "Reglas establecidas por individuos",
                        "Estándares de comportamiento a seguir en el trabajo",
                        "Leyes de tráfico",
                        "Regulaciones familiares",
                        "Estándares de comportamiento a seguir en el trabajo"
                },
                {
                        "¿La actitud positiva en el trabajo se muestra a través de qué?",
                        "Evitar tareas",
                        "Ser proactivo, cooperativo y responsable",
                        "Trabajar de manera superficial",
                        "Solo seguir órdenes",
                        "Ser proactivo, cooperativo y responsable"
                },
                {
                        "¿Qué es la productividad laboral?",
                        "Horas de trabajo",
                        "Eficiencia lograda en el proceso laboral",
                        "Salario recibido",
                        "Posición en la empresa",
                        "Eficiencia lograda en el proceso laboral"
                },
                {
                        "¿La elección de carrera debe basarse en qué factor?",
                        "Intereses, habilidades y necesidades sociales",
                        "Opiniones de amigos",
                        "Carreras de moda",
                        "Distancia geográfica",
                        "Intereses, habilidades y necesidades sociales"
                },
                {
                        "¿Qué son las habilidades blandas en el trabajo?",
                        "Habilidades profesionales",
                        "Habilidades de comunicación, trabajo en equipo, gestión del tiempo",
                        "Habilidades de máquinas",
                        "Habilidades manuales",
                        "Habilidades de comunicación, trabajo en equipo, gestión del tiempo"
                },
                {
                        "¿Cuál es el significado del espíritu de aprendizaje en el trabajo?",
                        "No es necesario",
                        "Ayuda a mejorar calificaciones y adaptarse a cambios",
                        "Ralentizar el trabajo",
                        "Perder tiempo",
                        "Ayuda a mejorar calificaciones y adaptarse a cambios"
                },
                {
                        "¿Cómo afecta el ambiente de trabajo a los trabajadores?",
                        "No tiene efecto",
                        "Afecta el rendimiento y espíritu laboral",
                        "Solo afecta el salario",
                        "Solo afecta las horas de trabajo",
                        "Afecta el rendimiento y espíritu laboral"
                },
                {
                        "¿Cuáles son los derechos de los trabajadores?",
                        "Solo trabajar sin descanso",
                        "Derechos garantizados y seguridad laboral",
                        "No se permiten quejas",
                        "No se permite licencia",
                        "Derechos garantizados y seguridad laboral"
                },
                {
                        "¿Cuáles son las obligaciones de los trabajadores?",
                        "Trabajar arbitrariamente",
                        "Cumplir con contratos y regulaciones laborales",
                        "Solo hacer lo que te gusta",
                        "No se necesita responsabilidad",
                        "Cumplir con contratos y regulaciones laborales"
                },
                {
                        "¿Qué significa el equilibrio trabajo-vida?",
                        "Trabajar más",
                        "Ayuda a mantener la salud y efectividad a largo plazo",
                        "Reducir ingresos",
                        "Trabajar menos",
                        "Ayuda a mantener la salud y efectividad a largo plazo"
                },
                {
                        "¿Qué es el desempleo?",
                        "No querer trabajar",
                        "Sin trabajo pero con necesidad de trabajar",
                        "Licencia a largo plazo",
                        "Trabajo independiente",
                        "Sin trabajo pero con necesidad de trabajar"
                },
                {
                        "¿Cuál es el propósito de la formación y desarrollo profesional?",
                        "Aumentar horas de trabajo",
                        "Mejorar habilidades y calificaciones profesionales",
                        "Reducir responsabilidad",
                        "Entretenimiento",
                        "Mejorar habilidades y calificaciones profesionales"
                },
                {
                        "En tu opinión, ¿cuál es el factor más importante para el éxito en el trabajo?",
                        "Suerte",
                        "Relaciones",
                        "Esfuerzo y responsabilidad",
                        "Estatus",
                        "Esfuerzo y responsabilidad"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Pháp
    public  String[][] workFrench() {
        return new String[][] {
                {
                        "Qu'est-ce que le travail?",
                        "Activité récréative personnelle",
                        "Activité de travail visant à générer des revenus ou de la valeur",
                        "Activité d'apprentissage",
                        "Activité de repos",
                        "Activité de travail visant à générer des revenus ou de la valeur"
                },
                {
                        "Quel est l'objectif principal du travail?",
                        "Divertissement",
                        "Générer des revenus et stabiliser la vie",
                        "Tuer le temps",
                        "Éviter le chômage",
                        "Générer des revenus et stabiliser la vie"
                },
                {
                        "Lequel des facteurs suivants est le plus important pour travailler efficacement?",
                        "Chance",
                        "Compétences et connaissances",
                        "Âge",
                        "Situation géographique",
                        "Compétences et connaissances"
                },
                {
                        "Qu'est-ce que la discipline de travail?",
                        "Travailler librement sans règles",
                        "Respecter les règlements, le temps et les responsabilités au travail",
                        "Ne travailler que selon les préférences",
                        "Travailler quand on en a envie",
                        "Respecter les règlements, le temps et les responsabilités au travail"
                },
                {
                        "Où l'esprit de responsabilité au travail se manifeste-t-il?",
                        "Travailler quand on est supervisé",
                        "Accomplir les tâches assignées à temps et avec qualité",
                        "Ne faire que du travail facile",
                        "Éviter les responsabilités",
                        "Accomplir les tâches assignées à temps et avec qualité"
                },
                {
                        "Quels sont les avantages du travail d'équipe?",
                        "Réduire la responsabilité individuelle",
                        "Augmenter l'efficacité et la qualité du travail",
                        "Facile de sécher le travail",
                        "Pas besoin de communiquer",
                        "Augmenter l'efficacité et la qualité du travail"
                },
                {
                        "À quoi servent les compétences de communication au travail?",
                        "Créer des conflits",
                        "Malentendus",
                        "Coordonner le travail efficacement",
                        "Travailler plus lentement",
                        "Coordonner le travail efficacement"
                },
                {
                        "Qu'est-ce que l'éthique professionnelle?",
                        "Règles établies par les individus",
                        "Normes de comportement à suivre au travail",
                        "Code de la route",
                        "Règlements familiaux",
                        "Normes de comportement à suivre au travail"
                },
                {
                        "L'attitude positive au travail se manifeste par quoi?",
                        "Éviter les tâches",
                        "Être proactif, coopératif et responsable",
                        "Travailler superficiellement",
                        "Ne suivre que les ordres",
                        "Être proactif, coopératif et responsable"
                },
                {
                        "Qu'est-ce que la productivité du travail?",
                        "Heures de travail",
                        "Efficacité obtenue dans le processus de travail",
                        "Salaire reçu",
                        "Position dans l'entreprise",
                        "Efficacité obtenue dans le processus de travail"
                },
                {
                        "Le choix de carrière devrait être basé sur quel facteur?",
                        "Intérêts, capacités et besoins sociaux",
                        "Opinions des amis",
                        "Carrières tendance",
                        "Distance géographique",
                        "Intérêts, capacités et besoins sociaux"
                },
                {
                        "Que sont les compétences douces au travail?",
                        "Compétences professionnelles",
                        "Compétences de communication, travail d'équipe, gestion du temps",
                        "Compétences machines",
                        "Compétences manuelles",
                        "Compétences de communication, travail d'équipe, gestion du temps"
                },
                {
                        "Quelle est la signification de l'esprit d'apprentissage au travail?",
                        "Pas nécessaire",
                        "Aide à améliorer les qualifications et à s'adapter aux changements",
                        "Ralentir le travail",
                        "Perdre du temps",
                        "Aide à améliorer les qualifications et à s'adapter aux changements"
                },
                {
                        "Comment l'environnement de travail affecte-t-il les travailleurs?",
                        "Aucun effet",
                        "Affecte la performance et l'esprit de travail",
                        "N'affecte que le salaire",
                        "N'affecte que les heures de travail",
                        "Affecte la performance et l'esprit de travail"
                },
                {
                        "Quels sont les droits des travailleurs?",
                        "Seulement travailler sans repos",
                        "Droits garantis et sécurité du travail",
                        "Aucune plainte autorisée",
                        "Aucun congé autorisé",
                        "Droits garantis et sécurité du travail"
                },
                {
                        "Quelles sont les obligations des travailleurs?",
                        "Travailler arbitrairement",
                        "Respecter les contrats et règlements du travail",
                        "Ne faire que ce qu'on aime",
                        "Aucune responsabilité nécessaire",
                        "Respecter les contrats et règlements du travail"
                },
                {
                        "Que signifie l'équilibre travail-vie?",
                        "Travailler plus",
                        "Aide à maintenir la santé et l'efficacité à long terme",
                        "Réduire les revenus",
                        "Travailler moins",
                        "Aide à maintenir la santé et l'efficacité à long terme"
                },
                {
                        "Qu'est-ce que le chômage?",
                        "Ne pas vouloir travailler",
                        "Pas d'emploi mais besoin de travailler",
                        "Congé à long terme",
                        "Travail indépendant",
                        "Pas d'emploi mais besoin de travailler"
                },
                {
                        "Quel est le but de la formation et développement professionnel?",
                        "Augmenter les heures de travail",
                        "Améliorer les compétences et qualifications professionnelles",
                        "Réduire la responsabilité",
                        "Divertissement",
                        "Améliorer les compétences et qualifications professionnelles"
                },
                {
                        "À votre avis, quel est le facteur le plus important pour réussir au travail?",
                        "Chance",
                        "Relations",
                        "Effort et responsabilité",
                        "Statut",
                        "Effort et responsabilité"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Đức
    public  String[][] workGerman() {
        return new String[][] {
                {
                        "Was ist Arbeit?",
                        "Persönliche Freizeitaktivität",
                        "Arbeitsaktivität zur Generierung von Einkommen oder Wert",
                        "Lernaktivität",
                        "Ruheaktivität",
                        "Arbeitsaktivität zur Generierung von Einkommen oder Wert"
                },
                {
                        "Was ist der Hauptzweck der Arbeit?",
                        "Unterhaltung",
                        "Einkommen generieren und das Leben stabilisieren",
                        "Zeit totschlagen",
                        "Arbeitslosigkeit vermeiden",
                        "Einkommen generieren und das Leben stabilisieren"
                },
                {
                        "Welcher der folgenden Faktoren ist am wichtigsten für effektives Arbeiten?",
                        "Glück",
                        "Fähigkeiten und Wissen",
                        "Alter",
                        "Geografische Lage",
                        "Fähigkeiten und Wissen"
                },
                {
                        "Was ist Arbeitsdisziplin?",
                        "Frei arbeiten ohne Regeln",
                        "Einhaltung von Vorschriften, Zeit und Verantwortlichkeiten bei der Arbeit",
                        "Nur nach Vorlieben arbeiten",
                        "Arbeiten wenn man Lust hat",
                        "Einhaltung von Vorschriften, Zeit und Verantwortlichkeiten bei der Arbeit"
                },
                {
                        "Wo zeigt sich der Geist der Verantwortung bei der Arbeit?",
                        "Arbeiten wenn überwacht",
                        "Zugewiesene Aufgaben rechtzeitig und qualitativ erledigen",
                        "Nur einfache Arbeit machen",
                        "Verantwortung vermeiden",
                        "Zugewiesene Aufgaben rechtzeitig und qualitativ erledigen"
                },
                {
                        "Was sind die Vorteile der Teamarbeit?",
                        "Individuelle Verantwortung reduzieren",
                        "Arbeitseffizienz und -qualität steigern",
                        "Einfach bei der Arbeit zu fehlen",
                        "Keine Kommunikation nötig",
                        "Arbeitseffizienz und -qualität steigern"
                },
                {
                        "Wobei helfen Kommunikationsfähigkeiten bei der Arbeit?",
                        "Konflikte schaffen",
                        "Missverständnisse",
                        "Arbeit effektiv koordinieren",
                        "Langsamer arbeiten",
                        "Arbeit effektiv koordinieren"
                },
                {
                        "Was ist Berufsethik?",
                        "Von Individuen aufgestellte Regeln",
                        "Verhaltensstandards die bei der Arbeit befolgt werden müssen",
                        "Verkehrsregeln",
                        "Familienregeln",
                        "Verhaltensstandards die bei der Arbeit befolgt werden müssen"
                },
                {
                        "Positive Arbeitseinstellung zeigt sich durch was?",
                        "Aufgaben vermeiden",
                        "Proaktiv, kooperativ und verantwortlich sein",
                        "Oberflächlich arbeiten",
                        "Nur Befehle befolgen",
                        "Proaktiv, kooperativ und verantwortlich sein"
                },
                {
                        "Was ist Arbeitsproduktivität?",
                        "Arbeitsstunden",
                        "Effizienz im Arbeitsprozess erreicht",
                        "Erhaltenes Gehalt",
                        "Position im Unternehmen",
                        "Effizienz im Arbeitsprozess erreicht"
                },
                {
                        "Berufswahl sollte auf welchem Faktor basieren?",
                        "Interessen, Fähigkeiten und gesellschaftliche Bedürfnisse",
                        "Meinungen von Freunden",
                        "Trendberufe",
                        "Geografische Entfernung",
                        "Interessen, Fähigkeiten und gesellschaftliche Bedürfnisse"
                },
                {
                        "Was sind Soft Skills bei der Arbeit?",
                        "Fachkenntnisse",
                        "Kommunikation, Teamarbeit, Zeitmanagement Fähigkeiten",
                        "Maschinenfähigkeiten",
                        "Handwerkliche Fähigkeiten",
                        "Kommunikation, Teamarbeit, Zeitmanagement Fähigkeiten"
                },
                {
                        "Was bedeutet Lerngeist bei der Arbeit?",
                        "Nicht notwendig",
                        "Hilft Qualifikationen zu verbessern und sich an Veränderungen anzupassen",
                        "Arbeit verlangsamen",
                        "Zeit verschwenden",
                        "Hilft Qualifikationen zu verbessern und sich an Veränderungen anzupassen"
                },
                {
                        "Wie beeinflusst das Arbeitsumfeld die Arbeiter?",
                        "Keine Wirkung",
                        "Beeinflusst Arbeitsleistung und -geist",
                        "Beeinflusst nur das Gehalt",
                        "Beeinflusst nur die Arbeitszeiten",
                        "Beeinflusst Arbeitsleistung und -geist"
                },
                {
                        "Was sind die Rechte der Arbeiter?",
                        "Nur arbeiten ohne Ruhe",
                        "Garantierte Rechte und Arbeitssicherheit",
                        "Keine Beschwerden erlaubt",
                        "Kein Urlaub erlaubt",
                        "Garantierte Rechte und Arbeitssicherheit"
                },
                {
                        "Was sind die Pflichten der Arbeiter?",
                        "Willkürlich arbeiten",
                        "Verträge und Arbeitsvorschriften einhalten",
                        "Nur tun was man mag",
                        "Keine Verantwortung nötig",
                        "Verträge und Arbeitsvorschriften einhalten"
                },
                {
                        "Was bedeutet Work-Life-Balance?",
                        "Mehr arbeiten",
                        "Hilft Gesundheit und langfristige Effektivität zu erhalten",
                        "Einkommen reduzieren",
                        "Weniger arbeiten",
                        "Hilft Gesundheit und langfristige Effektivität zu erhalten"
                },
                {
                        "Was ist Arbeitslosigkeit?",
                        "Nicht arbeiten wollen",
                        "Keine Arbeit aber Bedarf zu arbeiten",
                        "Langzeiturlaub",
                        "Freiberufliche Arbeit",
                        "Keine Arbeit aber Bedarf zu arbeiten"
                },
                {
                        "Was ist der Zweck der beruflichen Ausbildung und Entwicklung?",
                        "Arbeitsstunden erhöhen",
                        "Fähigkeiten und berufliche Qualifikationen verbessern",
                        "Verantwortung reduzieren",
                        "Unterhaltung",
                        "Fähigkeiten und berufliche Qualifikationen verbessern"
                },
                {
                        "Ihrer Meinung nach, was ist der wichtigste Faktor für Erfolg bei der Arbeit?",
                        "Glück",
                        "Beziehungen",
                        "Anstrengung und Verantwortung",
                        "Status",
                        "Anstrengung und Verantwortung"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Trung
    public  String[][] workChinese() {
        return new String[][] {
                {
                        "什么是工作？",
                        "个人娱乐活动",
                        "旨在创造收入或价值的劳动活动",
                        "学习活动",
                        "休息活动",
                        "旨在创造收入或价值的劳动活动"
                },
                {
                        "工作的主要目的是什么？",
                        "娱乐",
                        "创造收入和稳定生活",
                        "消磨时间",
                        "避免失业",
                        "创造收入和稳定生活"
                },
                {
                        "以下哪个因素对有效工作最重要？",
                        "运气",
                        "技能和知识",
                        "年龄",
                        "地理位置",
                        "技能和知识"
                },
                {
                        "什么是劳动纪律？",
                        "自由工作，没有规则",
                        "遵守工作中的规章、时间和责任",
                        "只按喜好工作",
                        "有兴致时才工作",
                        "遵守工作中的规章、时间和责任"
                },
                {
                        "工作中的责任精神体现在哪里？",
                        "在监督下工作",
                        "按时高质量完成分配的任务",
                        "只做容易的工作",
                        "推卸责任",
                        "按时高质量完成分配的任务"
                },
                {
                        "团队合作有什么好处？",
                        "减少个人责任",
                        "提高工作效率和质量",
                        "容易逃避工作",
                        "不需要沟通",
                        "提高工作效率和质量"
                },
                {
                        "工作中的沟通技能有什么帮助？",
                        "制造冲突",
                        "误解",
                        "有效协调工作",
                        "工作更慢",
                        "有效协调工作"
                },
                {
                        "什么是职业道德？",
                        "个人制定的规则",
                        "工作中需要遵守的行为标准",
                        "交通法规",
                        "家庭规定",
                        "工作中需要遵守的行为标准"
                },
                {
                        "积极的工作态度体现在什么方面？",
                        "逃避任务",
                        "主动、合作和负责任",
                        "敷衍工作",
                        "只听从命令",
                        "主动、合作和负责任"
                },
                {
                        "什么是劳动生产率？",
                        "工作时间",
                        "劳动过程中达到的效率",
                        "收到的工资",
                        "公司职位",
                        "劳动过程中达到的效率"
                },
                {
                        "职业选择应该基于什么因素？",
                        "兴趣、能力和社会需求",
                        "朋友的意见",
                        "热门职业",
                        "地理距离",
                        "兴趣、能力和社会需求"
                },
                {
                        "工作中的软技能是什么？",
                        "专业技能",
                        "沟通、团队合作、时间管理技能",
                        "机器技能",
                        "手工技能",
                        "沟通、团队合作、时间管理技能"
                },
                {
                        "工作中的学习精神有什么意义？",
                        "不必要",
                        "帮助提高资质和适应变化",
                        "使工作变慢",
                        "浪费时间",
                        "帮助提高资质和适应变化"
                },
                {
                        "工作环境如何影响工人？",
                        "没有影响",
                        "影响工作表现和精神",
                        "只影响工资",
                        "只影响工作时间",
                        "影响工作表现和精神"
                },
                {
                        "工人的权利是什么？",
                        "只工作不休息",
                        "保障权益和劳动安全",
                        "不允许投诉",
                        "不允许请假",
                        "保障权益和劳动安全"
                },
                {
                        "工人的义务是什么？",
                        "随意工作",
                        "遵守合同和劳动法规",
                        "只做自己喜欢的",
                        "不需要责任",
                        "遵守合同和劳动法规"
                },
                {
                        "工作与生活平衡意味着什么？",
                        "工作更多",
                        "有助于维持健康和长期效率",
                        "减少收入",
                        "工作更少",
                        "有助于维持健康和长期效率"
                },
                {
                        "什么是失业？",
                        "不想工作",
                        "没有工作但需要工作",
                        "长期休假",
                        "自由职业",
                        "没有工作但需要工作"
                },
                {
                        "职业培训和发展的目的是什么？",
                        "增加工作时间",
                        "提高技能和专业资质",
                        "减少责任",
                        "娱乐",
                        "提高技能和专业资质"
                },
                {
                        "您认为工作成功最重要的因素是什么？",
                        "运气",
                        "关系",
                        "努力和责任",
                        "地位",
                        "努力和责任"
                }
        };
    }
    // Hàm trả về câu hỏi tiếng Hàn Quốc
    private String[][] workKorean(){
        return new String[][] {
                {
                        "일이란 무엇인가요?",
                        "개인적인 오락 활동",
                        "수입이나 가치를 창출하는 노동 활동",
                        "학습 활동",
                        "휴식 활동",
                        "수입이나 가치를 창출하는 노동 활동"
                },
                {
                        "일의 주요 목적은 무엇인가요?",
                        "오락",
                        "수입을 창출하고 삶을 안정시키는 것",
                        "시간 보내기",
                        "실업 피하기",
                        "수입을 창출하고 삶을 안정시키는 것"
                },
                {
                        "다음 중 효과적인 일을 위해 가장 중요한 요소는?",
                        "운",
                        "기술과 지식",
                        "나이",
                        "지리적 위치",
                        "기술과 지식"
                },
                {
                        "노동 규율이란 무엇인가요?",
                        "규칙 없이 자유롭게 일하기",
                        "직장에서 규정, 시간 및 책임 준수",
                        "취향에 따라서만 일하기",
                        "기분이 날 때만 일하기",
                        "직장에서 규정, 시간 및 책임 준수"
                },
                {
                        "직장에서의 책임감은 어디에서 나타나나요?",
                        "감독받을 때 일하기",
                        "맡은 업무를 정시에 품질 좋게 완료하기",
                        "쉬운 일만 하기",
                        "책임 회피하기",
                        "맡은 업무를 정시에 품질 좋게 완료하기"
                },
                {
                        "팀워크의 이점은 무엇인가요?",
                        "개인 책임 줄이기",
                        "업무 효율성과 품질 향상",
                        "일하지 않기 쉬움",
                        "소통할 필요 없음",
                        "업무 효율성과 품질 향상"
                },
                {
                        "직장에서의 의사소통 기술은 무엇을 돕나요?",
                        "갈등 만들기",
                        "오해",
                        "업무 효과적으로 조정하기",
                        "더 느리게 일하기",
                        "업무 효과적으로 조정하기"
                },
                {
                        "직업 윤리란 무엇인가요?",
                        "개인이 정한 규칙",
                        "직장에서 따라야 할 행동 기준",
                        "교통법",
                        "가족 규정",
                        "직장에서 따라야 할 행동 기준"
                },
                {
                        "긍정적인 업무 태도는 무엇을 통해 나타나나요?",
                        "업무 회피",
                        "적극적이고 협력적이며 책임감 있는 태도",
                        "건성으로 일하기",
                        "명령만 따르기",
                        "적극적이고 협력적이며 책임감 있는 태도"
                },
                {
                        "노동 생산성이란 무엇인가요?",
                        "근무 시간",
                        "노동 과정에서 달성된 효율성",
                        "받은 급여",
                        "회사에서의 직책",
                        "노동 과정에서 달성된 효율성"
                },
                {
                        "직업 선택은 어떤 요소에 기반해야 하나요?",
                        "관심사, 능력 및 사회적 요구",
                        "친구들의 의견",
                        "유행하는 직업",
                        "지리적 거리",
                        "관심사, 능력 및 사회적 요구"
                },
                {
                        "직장에서의 소프트 스킬이란 무엇인가요?",
                        "전문 기술",
                        "의사소통, 팀워크, 시간 관리 기술",
                        "기계 기술",
                        "수작업 기술",
                        "의사소통, 팀워크, 시간 관리 기술"
                },
                {
                        "직장에서의 학습 정신은 어떤 의미가 있나요?",
                        "필요 없음",
                        "자격을 향상시키고 변화에 적응하는 데 도움",
                        "업무 속도 늦추기",
                        "시간 낭비",
                        "자격을 향상시키고 변화에 적응하는 데 도움"
                },
                {
                        "업무 환경은 근로자에게 어떤 영향을 주나요?",
                        "영향 없음",
                        "업무 성과와 정신에 영향",
                        "급여에만 영향",
                        "근무 시간에만 영향",
                        "업무 성과와 정신에 영향"
                },
                {
                        "근로자의 권리는 무엇인가요?",
                        "쉬지 않고 일만 하기",
                        "권익과 노동 안전 보장",
                        "불만 제기 불허",
                        "휴가 불허",
                        "권익과 노동 안전 보장"
                },
                {
                        "근로자의 의무는 무엇인가요?",
                        "마음대로 일하기",
                        "계약과 노동 규정 준수",
                        "좋아하는 일만 하기",
                        "책임질 필요 없음",
                        "계약과 노동 규정 준수"
                },
                {
                        "일과 삶의 균형은 무엇을 의미하나요?",
                        "더 많이 일하기",
                        "건강과 장기적 효과 유지에 도움",
                        "수입 줄이기",
                        "덜 일하기",
                        "건강과 장기적 효과 유지에 도움"
                },
                {
                        "실업이란 무엇인가요?",
                        "일하고 싶지 않음",
                        "일자리가 없지만 일할 필요가 있음",
                        "장기 휴가",
                        "자유직업",
                        "일자리가 없지만 일할 필요가 있음"
                },
                {
                        "직업 교육과 개발의 목적은 무엇인가요?",
                        "근무 시간 늘리기",
                        "기술과 전문 자격 향상",
                        "책임 줄이기",
                        "오락",
                        "기술과 전문 자격 향상"
                },
                {
                        "당신 생각에 직장에서 성공하기 위한 가장 중요한 요소는 무엇인가요?",
                        "운",
                        "관계",
                        "노력과 책임",
                        "지위",
                        "노력과 책임"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Nga
    private String[][] workRussian(){
        return new String[][] {
                {
                        "Что такое работа?",
                        "Личная развлекательная деятельность",
                        "Трудовая деятельность, направленная на получение дохода или создание ценности",
                        "Учебная деятельность",
                        "Деятельность отдыха",
                        "Трудовая деятельность, направленная на получение дохода или создание ценности"
                },
                {
                        "Какова основная цель работы?",
                        "Развлечение",
                        "Получение дохода и стабилизация жизни",
                        "Убить время",
                        "Избежать безработицы",
                        "Получение дохода и стабилизация жизни"
                },
                {
                        "Какой из следующих факторов наиболее важен для эффективной работы?",
                        "Удача",
                        "Навыки и знания",
                        "Возраст",
                        "Географическое положение",
                        "Навыки и знания"
                },
                {
                        "Что такое трудовая дисциплина?",
                        "Работать свободно без правил",
                        "Соблюдение правил, времени и обязанностей на работе",
                        "Работать только по предпочтениям",
                        "Работать когда есть настроение",
                        "Соблюдение правил, времени и обязанностей на работе"
                },
                {
                        "Где проявляется дух ответственности на работе?",
                        "Работать под надзором",
                        "Выполнять порученные задачи вовремя и качественно",
                        "Делать только легкую работу",
                        "Избегать ответственности",
                        "Выполнять порученные задачи вовремя и качественно"
                },
                {
                        "Каковы преимущества командной работы?",
                        "Уменьшить индивидуальную ответственность",
                        "Повысить эффективность и качество работы",
                        "Легко прогуливать работу",
                        "Не нужно общаться",
                        "Повысить эффективность и качество работы"
                },
                {
                        "Чем помогают навыки общения на работе?",
                        "Создавать конфликты",
                        "Недопонимание",
                        "Эффективно координировать работу",
                        "Работать медленнее",
                        "Эффективно координировать работу"
                },
                {
                        "Что такое профессиональная этика?",
                        "Правила, установленные людьми",
                        "Стандарты поведения, которым нужно следовать на работе",
                        "Правила дорожного движения",
                        "Семейные правила",
                        "Стандарты поведения, которым нужно следовать на работе"
                },
                {
                        "Позитивное отношение к работе проявляется через что?",
                        "Избегание задач",
                        "Быть активным, сотрудничающим и ответственным",
                        "Работать поверхностно",
                        "Только выполнять приказы",
                        "Быть активным, сотрудничающим и ответственным"
                },
                {
                        "Что такое производительность труда?",
                        "Рабочие часы",
                        "Эффективность, достигнутая в трудовом процессе",
                        "Полученная зарплата",
                        "Должность в компании",
                        "Эффективность, достигнутая в трудовом процессе"
                },
                {
                        "Выбор карьеры должен основываться на каком факторе?",
                        "Интересы, способности и социальные потребности",
                        "Мнения друзей",
                        "Модные профессии",
                        "Географическое расстояние",
                        "Интересы, способности и социальные потребности"
                },
                {
                        "Что такое гибкие навыки на работе?",
                        "Профессиональные навыки",
                        "Навыки общения, командной работы, управления временем",
                        "Навыки работы с машинами",
                        "Ручные навыки",
                        "Навыки общения, командной работы, управления временем"
                },
                {
                        "Каково значение духа обучения на работе?",
                        "Не нужно",
                        "Помогает повысить квалификацию и адаптироваться к изменениям",
                        "Замедлить работу",
                        "Тратить время",
                        "Помогает повысить квалификацию и адаптироваться к изменениям"
                },
                {
                        "Как рабочая среда влияет на работников?",
                        "Никак не влияет",
                        "Влияет на производительность и дух работы",
                        "Влияет только на зарплату",
                        "Влияет только на рабочие часы",
                        "Влияет на производительность и дух работы"
                },
                {
                        "Каковы права работников?",
                        "Только работать без отдыха",
                        "Гарантированные права и безопасность труда",
                        "Жалобы не допускаются",
                        "Отпуск не разрешен",
                        "Гарантированные права и безопасность труда"
                },
                {
                        "Каковы обязанности работников?",
                        "Работать произвольно",
                        "Соблюдать договоры и трудовые правила",
                        "Делать только то, что нравится",
                        "Нет нужды в ответственности",
                        "Соблюдать договоры и трудовые правила"
                },
                {
                        "Что означает баланс работы и жизни?",
                        "Работать больше",
                        "Помогает поддерживать здоровье и долгосрочную эффективность",
                        "Уменьшить доход",
                        "Работать меньше",
                        "Помогает поддерживать здоровье и долгосрочную эффективность"
                },
                {
                        "Что такое безработица?",
                        "Не хотеть работать",
                        "Нет работы, но есть потребность работать",
                        "Долгосрочный отпуск",
                        "Свободная работа",
                        "Нет работы, но есть потребность работать"
                },
                {
                        "Какова цель профессиональной подготовки и развития?",
                        "Увеличить рабочие часы",
                        "Улучшить навыки и профессиональную квалификацию",
                        "Уменьшить ответственность",
                        "Развлечение",
                        "Улучшить навыки и профессиональную квалификацию"
                },
                {
                        "По вашему мнению, какой самый важный фактор для успеха на работе?",
                        "Удача",
                        "Отношения",
                        "Усилия и ответственность",
                        "Статус",
                        "Усилия и ответственность"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Hindi
    private String[][] workHindi(){
        return new String[][] {
                {
                        "काम क्या है?",
                        "व्यक्तिगत मनोरंजन गतिविधि",
                        "आय या मूल्य उत्पन्न करने का श्रम गतिविधि",
                        "सीखने की गतिविधि",
                        "आराम की गतिविधि",
                        "आय या मूल्य उत्पन्न करने का श्रम गतिविधि"
                },
                {
                        "काम का मुख्य उद्देश्य क्या है?",
                        "मनोरंजन",
                        "आय उत्पन्न करना और जीवन स्थिर करना",
                        "समय काटना",
                        "बेरोजगारी से बचना",
                        "आय उत्पन्न करना और जीवन स्थिर करना"
                },
                {
                        "प्रभावी काम के लिए निम्न में से कौन सा कारक सबसे महत्वपूर्ण है?",
                        "भाग्य",
                        "कौशल और ज्ञान",
                        "उम्र",
                        "भौगोलिक स्थिति",
                        "कौशल और ज्ञान"
                },
                {
                        "श्रम अनुशासन क्या है?",
                        "बिना नियमों के स्वतंत्र रूप से काम करना",
                        "काम में नियम, समय और जिम्मेदारियों का पालन",
                        "केवल पसंद के अनुसार काम करना",
                        "मूड होने पर काम करना",
                        "काम में नियम, समय और जिम्मेदारियों का पालन"
                },
                {
                        "काम में जिम्मेदारी की भावना कहाँ दिखती है?",
                        "निगरानी में काम करना",
                        "सौंपे गए कार्य समय पर और गुणवत्ता के साथ पूरे करना",
                        "केवल आसान काम करना",
                        "जिम्मेदारी से बचना",
                        "सौंपे गए कार्य समय पर और गुणवत्ता के साथ पूरे करना"
                },
                {
                        "टीमवर्क के क्या फायदे हैं?",
                        "व्यक्तिगत जिम्मेदारी कम करना",
                        "काम की दक्षता और गुणवत्ता बढ़ाना",
                        "काम से बचना आसान",
                        "संवाद की जरूरत नहीं",
                        "काम की दक्षता और गुणवत्ता बढ़ाना"
                },
                {
                        "काम में संवाद कौशल क्या मदद करता है?",
                        "संघर्ष पैदा करना",
                        "गलतफहमी",
                        "काम को प्रभावी रूप से समन्वित करना",
                        "धीमा काम करना",
                        "काम को प्रभावी रूप से समन्वित करना"
                },
                {
                        "व्यावसायिक नैतिकता क्या है?",
                        "व्यक्तियों द्वारा बनाए गए नियम",
                        "काम में अनुसरण किए जाने वाले व्यवहार मानक",
                        "यातायात नियम",
                        "पारिवारिक नियम",
                        "काम में अनुसरण किए जाने वाले व्यवहार मानक"
                },
                {
                        "सकारात्मक कार्य दृष्टिकोण किस से दिखता है?",
                        "कार्यों से बचना",
                        "सक्रिय, सहयोगी और जिम्मेदार होना",
                        "दिखावटी काम करना",
                        "केवल आदेशों का पालन",
                        "सक्रिय, सहयोगी और जिम्मेदार होना"
                },
                {
                        "श्रम उत्पादकता क्या है?",
                        "काम के घंटे",
                        "श्रम प्रक्रिया में प्राप्त दक्षता",
                        "प्राप्त वेतन",
                        "कंपनी में पद",
                        "श्रम प्रक्रिया में प्राप्त दक्षता"
                },
                {
                        "कैरियर चुनाव किस कारक पर आधारित होना चाहिए?",
                        "रुचियाँ, क्षमताएं और सामाजिक आवश्यकताएं",
                        "दोस्तों की राय",
                        "ट्रेंडिंग करियर",
                        "भौगोलिक दूरी",
                        "रुचियाँ, क्षमताएं और सामाजिक आवश्यकताएं"
                },
                {
                        "काम में सॉफ्ट स्किल्स क्या हैं?",
                        "व्यावसायिक कौशल",
                        "संवाद, टीमवर्क, समय प्रबंधन कौशल",
                        "मशीन कौशल",
                        "हस्तकौशल",
                        "संवाद, टीमवर्क, समय प्रबंधन कौशल"
                },
                {
                        "काम में सीखने की भावना का क्या मतलब है?",
                        "जरूरी नहीं",
                        "योग्यता सुधारने और बदलाव के अनुकूल होने में मदद",
                        "काम धीमा करना",
                        "समय बर्बाद करना",
                        "योग्यता सुधारने और बदलाव के अनुकूल होने में मदद"
                },
                {
                        "कार्य वातावरण श्रमिकों को कैसे प्रभावित करता है?",
                        "कोई प्रभाव नहीं",
                        "कार्य प्रदर्शन और भावना को प्रभावित करता है",
                        "केवल वेतन प्रभावित करता है",
                        "केवल काम के घंटे प्रभावित करता है",
                        "कार्य प्रदर्शन और भावना को प्रभावित करता है"
                },
                {
                        "श्रमिकों के अधिकार क्या हैं?",
                        "केवल बिना आराम के काम करना",
                        "गारंटीशुदा अधिकार और श्रम सुरक्षा",
                        "शिकायत की अनुमति नहीं",
                        "छुट्टी की अनुमति नहीं",
                        "गारंटीशुदा अधिकार और श्रम सुरक्षा"
                },
                {
                        "श्रमिकों के दायित्व क्या हैं?",
                        "मनमाने तरीके से काम करना",
                        "अनुबंध और श्रम नियमों का पालन",
                        "केवल पसंदीदा काम करना",
                        "जिम्मेदारी की जरूरत नहीं",
                        "अनुबंध और श्रम नियमों का पालन"
                },
                {
                        "कार्य-जीवन संतुलन का क्या मतलब है?",
                        "अधिक काम करना",
                        "स्वास्थ्य और दीर्घकालिक प्रभावशीलता बनाए रखने में मदद",
                        "आय कम करना",
                        "कम काम करना",
                        "स्वास्थ्य और दीर्घकालिक प्रभावशीलता बनाए रखने में मदद"
                },
                {
                        "बेरोजगारी क्या है?",
                        "काम नहीं करना चाहना",
                        "नौकरी नहीं है लेकिन काम करने की जरूरत है",
                        "लंबी छुट्टी",
                        "स्वतंत्र काम",
                        "नौकरी नहीं है लेकिन काम करने की जरूरत है"
                },
                {
                        "व्यावसायिक प्रशिक्षण और विकास का उद्देश्य क्या है?",
                        "काम के घंटे बढ़ाना",
                        "कौशल और व्यावसायिक योग्यता सुधारना",
                        "जिम्मेदारी कम करना",
                        "मनोरंजन",
                        "कौशल और व्यावसायिक योग्यता सुधारना"
                },
                {
                        "आपकी राय में, काम में सफलता के लिए सबसे महत्वपूर्ण कारक क्या है?",
                        "भाग्य",
                        "रिश्ते",
                        "प्रयास और जिम्मेदारी",
                        "स्थिति",
                        "प्रयास और जिम्मेदारी"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Nhật
    private String[][] workJapanese(){
        return new String[][] {
                {
                        "仕事とは何ですか？",
                        "個人的な娯楽活動",
                        "収入や価値を生み出すための労働活動",
                        "学習活動",
                        "休息活動",
                        "収入や価値を生み出すための労働活動"
                },
                {
                        "仕事の主な目的は何ですか？",
                        "娯楽",
                        "収入を生み出し生活を安定させること",
                        "時間つぶし",
                        "失業を避けること",
                        "収入を生み出し生活を安定させること"
                },
                {
                        "効果的に働くために最も重要な要素は次のうちどれですか？",
                        "運",
                        "スキルと知識",
                        "年齢",
                        "地理的位置",
                        "スキルと知識"
                },
                {
                        "労働規律とは何ですか？",
                        "ルールなしで自由に働くこと",
                        "職場での規則、時間、責任の遵守",
                        "好みに応じてのみ働くこと",
                        "気が向いた時に働くこと",
                        "職場での規則、時間、責任の遵守"
                },
                {
                        "職場での責任感はどこに現れますか？",
                        "監督されている時に働くこと",
                        "割り当てられたタスクを時間通りに質良く完了すること",
                        "簡単な仕事だけをすること",
                        "責任を回避すること",
                        "割り当てられたタスクを時間通りに質良く完了すること"
                },
                {
                        "チームワークの利点は何ですか？",
                        "個人の責任を減らすこと",
                        "仕事の効率と質を向上させること",
                        "仕事をサボりやすいこと",
                        "コミュニケーションが不要",
                        "仕事の効率と質を向上させること"
                },
                {
                        "職場でのコミュニケーションスキルは何に役立ちますか？",
                        "対立を作ること",
                        "誤解",
                        "仕事を効果的に調整すること",
                        "より遅く働くこと",
                        "仕事を効果的に調整すること"
                },
                {
                        "職業倫理とは何ですか？",
                        "個人が設定したルール",
                        "職場で従うべき行動基準",
                        "交通法",
                        "家族の規則",
                        "職場で従うべき行動基準"
                },
                {
                        "積極的な仕事態度は何を通して示されますか？",
                        "タスクを避けること",
                        "積極的で協力的で責任感があること",
                        "表面的に仕事をすること",
                        "命令に従うだけ",
                        "積極的で協力的で責任感があること"
                },
                {
                        "労働生産性とは何ですか？",
                        "労働時間",
                        "労働プロセスで達成された効率",
                        "受け取った給与",
                        "会社での地位",
                        "労働プロセスで達成された効率"
                },
                {
                        "キャリア選択はどの要因に基づくべきですか？",
                        "興味、能力、社会的ニーズ",
                        "友人の意見",
                        "トレンドのキャリア",
                        "地理的距離",
                        "興味、能力、社会的ニーズ"
                },
                {
                        "職場でのソフトスキルとは何ですか？",
                        "専門スキル",
                        "コミュニケーション、チームワーク、時間管理スキル",
                        "機械スキル",
                        "手作業スキル",
                        "コミュニケーション、チームワーク、時間管理スキル"
                },
                {
                        "職場での学習精神にはどのような意味がありますか？",
                        "必要ない",
                        "資格を向上させ変化に適応するのに役立つ",
                        "仕事を遅くする",
                        "時間の無駄",
                        "資格を向上させ変化に適応するのに役立つ"
                },
                {
                        "職場環境は労働者にどのような影響を与えますか？",
                        "影響なし",
                        "仕事のパフォーマンスと精神に影響",
                        "給与にのみ影響",
                        "労働時間にのみ影響",
                        "仕事のパフォーマンスと精神に影響"
                },
                {
                        "労働者の権利とは何ですか？",
                        "休まずに働くだけ",
                        "権益と労働安全の保障",
                        "苦情は認められない",
                        "休暇は認められない",
                        "権益と労働安全の保障"
                },
                {
                        "労働者の義務とは何ですか？",
                        "恣意的に働くこと",
                        "契約と労働規則の遵守",
                        "好きな仕事だけをすること",
                        "責任は不要",
                        "契約と労働規則の遵守"
                },
                {
                        "ワークライフバランスとは何を意味しますか？",
                        "もっと働くこと",
                        "健康と長期的効果の維持に役立つ",
                        "収入を減らすこと",
                        "少なく働くこと",
                        "健康と長期的効果の維持に役立つ"
                },
                {
                        "失業とは何ですか？",
                        "働きたくないこと",
                        "仕事がないが働く必要があること",
                        "長期休暇",
                        "フリーランス",
                        "仕事がないが働く必要があること"
                },
                {
                        "職業訓練と開発の目的は何ですか？",
                        "労働時間を増やすこと",
                        "スキルと専門資格の向上",
                        "責任を減らすこと",
                        "娯楽",
                        "スキルと専門資格の向上"
                },
                {
                        "あなたの意見では、職場で成功するための最も重要な要因は何ですか？",
                        "運",
                        "関係",
                        "努力と責任",
                        "地位",
                        "努力と責任"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Bồ Đào Nha
    private String[][] workPortuguese(){
        return new String[][] {
                {
                        "O que é trabalho?",
                        "Atividade recreativa pessoal",
                        "Atividade laboral destinada a gerar renda ou valor",
                        "Atividade de aprendizagem",
                        "Atividade de descanso",
                        "Atividade laboral destinada a gerar renda ou valor"
                },
                {
                        "Qual é o principal objetivo do trabalho?",
                        "Entretenimento",
                        "Gerar renda e estabilizar a vida",
                        "Matar o tempo",
                        "Evitar o desemprego",
                        "Gerar renda e estabilizar a vida"
                },
                {
                        "Qual dos seguintes fatores é mais importante para trabalhar eficazmente?",
                        "Sorte",
                        "Habilidades e conhecimento",
                        "Idade",
                        "Localização geográfica",
                        "Habilidades e conhecimento"
                },
                {
                        "O que é disciplina de trabalho?",
                        "Trabalhar livremente sem regras",
                        "Cumprimento de regulamentos, tempo e responsabilidades no trabalho",
                        "Trabalhar apenas conforme as preferências",
                        "Trabalhar quando se tem vontade",
                        "Cumprimento de regulamentos, tempo e responsabilidades no trabalho"
                },
                {
                        "Onde se mostra o espírito de responsabilidade no trabalho?",
                        "Trabalhar quando supervisionado",
                        "Completar tarefas designadas no prazo e com qualidade",
                        "Fazer apenas trabalho fácil",
                        "Evitar responsabilidades",
                        "Completar tarefas designadas no prazo e com qualidade"
                },
                {
                        "Quais são os benefícios do trabalho em equipe?",
                        "Reduzir a responsabilidade individual",
                        "Aumentar a eficiência e qualidade do trabalho",
                        "Fácil faltar ao trabalho",
                        "Não precisar se comunicar",
                        "Aumentar a eficiência e qualidade do trabalho"
                },
                {
                        "Para que ajudam as habilidades de comunicação no trabalho?",
                        "Criar conflitos",
                        "Mal-entendidos",
                        "Coordenar o trabalho eficazmente",
                        "Trabalhar mais devagar",
                        "Coordenar o trabalho eficazmente"
                },
                {
                        "O que é ética profissional?",
                        "Regras estabelecidas por indivíduos",
                        "Padrões de comportamento a serem seguidos no trabalho",
                        "Leis de trânsito",
                        "Regulamentos familiares",
                        "Padrões de comportamento a serem seguidos no trabalho"
                },
                {
                        "Atitude positiva no trabalho se mostra através do quê?",
                        "Evitar tarefas",
                        "Ser proativo, cooperativo e responsável",
                        "Trabalhar superficialmente",
                        "Apenas seguir ordens",
                        "Ser proativo, cooperativo e responsável"
                },
                {
                        "O que é produtividade do trabalho?",
                        "Horas de trabalho",
                        "Eficiência alcançada no processo de trabalho",
                        "Salário recebido",
                        "Posição na empresa",
                        "Eficiência alcançada no processo de trabalho"
                },
                {
                        "A escolha de carreira deve ser baseada em qual fator?",
                        "Interesses, habilidades e necessidades sociais",
                        "Opiniões dos amigos",
                        "Carreiras em alta",
                        "Distância geográfica",
                        "Interesses, habilidades e necessidades sociais"
                },
                {
                        "O que são habilidades interpessoais no trabalho?",
                        "Habilidades profissionais",
                        "Habilidades de comunicação, trabalho em equipe, gestão de tempo",
                        "Habilidades de máquinas",
                        "Habilidades manuais",
                        "Habilidades de comunicação, trabalho em equipe, gestão de tempo"
                },
                {
                        "Qual é o significado do espírito de aprendizagem no trabalho?",
                        "Não necessário",
                        "Ajuda a melhorar qualificações e adaptar-se a mudanças",
                        "Atrasar o trabalho",
                        "Perder tempo",
                        "Ajuda a melhorar qualificações e adaptar-se a mudanças"
                },
                {
                        "Como o ambiente de trabalho afeta os trabalhadores?",
                        "Não tem efeito",
                        "Afeta o desempenho e espírito de trabalho",
                        "Afeta apenas o salário",
                        "Afeta apenas as horas de trabalho",
                        "Afeta o desempenho e espírito de trabalho"
                },
                {
                        "Quais são os direitos dos trabalhadores?",
                        "Apenas trabalhar sem descanso",
                        "Direitos garantidos e segurança do trabalho",
                        "Não são permitidas reclamações",
                        "Não são permitidas licenças",
                        "Direitos garantidos e segurança do trabalho"
                },
                {
                        "Quais são as obrigações dos trabalhadores?",
                        "Trabalhar arbitrariamente",
                        "Cumprir contratos e regulamentos de trabalho",
                        "Fazer apenas o que se gosta",
                        "Não precisa de responsabilidade",
                        "Cumprir contratos e regulamentos de trabalho"
                },
                {
                        "O que significa equilíbrio trabalho-vida?",
                        "Trabalhar mais",
                        "Ajuda a manter a saúde e eficácia a longo prazo",
                        "Reduzir a renda",
                        "Trabalhar menos",
                        "Ajuda a manter a saúde e eficácia a longo prazo"
                },
                {
                        "O que é desemprego?",
                        "Não querer trabalhar",
                        "Sem emprego mas com necessidade de trabalhar",
                        "Licença de longo prazo",
                        "Trabalho freelance",
                        "Sem emprego mas com necessidade de trabalhar"
                },
                {
                        "Qual é o propósito do treinamento e desenvolvimento profissional?",
                        "Aumentar as horas de trabalho",
                        "Melhorar habilidades e qualificações profissionais",
                        "Reduzir responsabilidade",
                        "Entretenimento",
                        "Melhorar habilidades e qualificações profissionais"
                },
                {
                        "Na sua opinião, qual é o fator mais importante para o sucesso no trabalho?",
                        "Sorte",
                        "Relacionamentos",
                        "Esforço e responsabilidade",
                        "Status",
                        "Esforço e responsabilidade"
                }
        };
    }
    // Hàm trả về câu hỏi tiếng Hà Lan
    private String[][] workDutch(){
        return new String[][] {
                {
                        "Wat is werk?",
                        "Persoonlijke recreatieve activiteit",
                        "Arbeidsactiviteit gericht op het genereren van inkomen of waarde",
                        "Leeractiviteit",
                        "Rustactiviteit",
                        "Arbeidsactiviteit gericht op het genereren van inkomen of waarde"
                },
                {
                        "Wat is het hoofddoel van werk?",
                        "Entertainment",
                        "Inkomen genereren en het leven stabiliseren",
                        "Tijd doden",
                        "Werkloosheid vermijden",
                        "Inkomen genereren en het leven stabiliseren"
                },
                {
                        "Welke van de volgende factoren is het belangrijkst voor effectief werken?",
                        "Geluk",
                        "Vaardigheden en kennis",
                        "Leeftijd",
                        "Geografische locatie",
                        "Vaardigheden en kennis"
                },
                {
                        "Wat is arbeidsdiscipline?",
                        "Vrij werken zonder regels",
                        "Naleving van regels, tijd en verantwoordelijkheden op het werk",
                        "Alleen werken volgens voorkeuren",
                        "Werken wanneer je zin hebt",
                        "Naleving van regels, tijd en verantwoordelijkheden op het werk"
                },
                {
                        "Waar toont zich de geest van verantwoordelijkheid op het werk?",
                        "Werken onder toezicht",
                        "Toegewezen taken op tijd en met kwaliteit voltooien",
                        "Alleen makkelijk werk doen",
                        "Verantwoordelijkheden ontlopen",
                        "Toegewezen taken op tijd en met kwaliteit voltooien"
                },
                {
                        "Wat zijn de voordelen van teamwerk?",
                        "Individuele verantwoordelijkheid verminderen",
                        "Werkefficiëntie en kwaliteit verhogen",
                        "Makkelijk spijbelen van werk",
                        "Geen communicatie nodig",
                        "Werkefficiëntie en kwaliteit verhogen"
                },
                {
                        "Waarmee helpen communicatievaardigheden op het werk?",
                        "Conflicten creëren",
                        "Misverstanden",
                        "Werk effectief coördineren",
                        "Langzamer werken",
                        "Werk effectief coördineren"
                },
                {
                        "Wat is beroepsethiek?",
                        "Regels opgesteld door individuen",
                        "Gedragsnormen die gevolgd moeten worden op het werk",
                        "Verkeersregels",
                        "Familieregels",
                        "Gedragsnormen die gevolgd moeten worden op het werk"
                },
                {
                        "Positieve werkhouding wordt getoond door wat?",
                        "Taken vermijden",
                        "Proactief, coöperatief en verantwoordelijk zijn",
                        "Oppervlakkig werken",
                        "Alleen bevelen opvolgen",
                        "Proactief, coöperatief en verantwoordelijk zijn"
                },
                {
                        "Wat is arbeidsproductiviteit?",
                        "Werkuren",
                        "Efficiëntie behaald in het arbeidsproces",
                        "Ontvangen salaris",
                        "Positie in bedrijf",
                        "Efficiëntie behaald in het arbeidsproces"
                },
                {
                        "Loopbaankeuze moet gebaseerd zijn op welke factor?",
                        "Interesses, vaardigheden en maatschappelijke behoeften",
                        "Meningen van vrienden",
                        "Trendy carrières",
                        "Geografische afstand",
                        "Interesses, vaardigheden en maatschappelijke behoeften"
                },
                {
                        "Wat zijn zachte vaardigheden op het werk?",
                        "Professionele vaardigheden",
                        "Communicatie, teamwerk, tijdbeheer vaardigheden",
                        "Machinevaardigheden",
                        "Handvaardigheden",
                        "Communicatie, teamwerk, tijdbeheer vaardigheden"
                },
                {
                        "Wat betekent leergeest op het werk?",
                        "Niet nodig",
                        "Helpt kwalificaties verbeteren en aanpassen aan veranderingen",
                        "Werk vertragen",
                        "Tijd verspillen",
                        "Helpt kwalificaties verbeteren en aanpassen aan veranderingen"
                },
                {
                        "Hoe beïnvloedt de werkomgeving werknemers?",
                        "Geen effect",
                        "Beïnvloedt werkprestaties en werkgeest",
                        "Beïnvloedt alleen salaris",
                        "Beïnvloedt alleen werkuren",
                        "Beïnvloedt werkprestaties en werkgeest"
                },
                {
                        "Wat zijn de rechten van werknemers?",
                        "Alleen werken zonder rust",
                        "Gegarandeerde rechten en arbeidsveiligheid",
                        "Geen klachten toegestaan",
                        "Geen verlof toegestaan",
                        "Gegarandeerde rechten en arbeidsveiligheid"
                },
                {
                        "Wat zijn de plichten van werknemers?",
                        "Willekeurig werken",
                        "Contracten en arbeidsregels naleven",
                        "Alleen doen wat je leuk vindt",
                        "Geen verantwoordelijkheid nodig",
                        "Contracten en arbeidsregels naleven"
                },
                {
                        "Wat betekent werk-privé balans?",
                        "Meer werken",
                        "Helpt gezondheid en langetermijneffectiviteit behouden",
                        "Inkomen verminderen",
                        "Minder werken",
                        "Helpt gezondheid en langetermijneffectiviteit behouden"
                },
                {
                        "Wat is werkloosheid?",
                        "Niet willen werken",
                        "Geen baan maar wel behoefte om te werken",
                        "Langdurig verlof",
                        "Freelance werk",
                        "Geen baan maar wel behoefte om te werken"
                },
                {
                        "Wat is het doel van professionele training en ontwikkeling?",
                        "Werkuren verhogen",
                        "Vaardigheden en professionele kwalificaties verbeteren",
                        "Verantwoordelijkheid verminderen",
                        "Entertainment",
                        "Vaardigheden en professionele kwalificaties verbeteren"
                },
                {
                        "Wat is volgens u de belangrijkste factor voor succes op het werk?",
                        "Geluk",
                        "Relaties",
                        "Inspanning en verantwoordelijkheid",
                        "Status",
                        "Inspanning en verantwoordelijkheid"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Ả Rập
    private String[][] workArabic(){
        return new String[][] {
                {
                        "ما هو العمل؟",
                        "نشاط ترفيهي شخصي",
                        "نشاط عمالي يهدف إلى توليد الدخل أو القيمة",
                        "نشاط تعليمي",
                        "نشاط راحة",
                        "نشاط عمالي يهدف إلى توليد الدخل أو القيمة"
                },
                {
                        "ما هو الهدف الأساسي من العمل؟",
                        "التسلية",
                        "توليد الدخل وتحقيق الاستقرار في الحياة",
                        "قضاء الوقت",
                        "تجنب البطالة",
                        "توليد الدخل وتحقيق الاستقرار في الحياة"
                },
                {
                        "أي من العوامل التالية هو الأهم للعمل الفعال؟",
                        "الحظ",
                        "المهارات والمعرفة",
                        "العمر",
                        "الموقع الجغرافي",
                        "المهارات والمعرفة"
                },
                {
                        "ما هو انضباط العمل؟",
                        "العمل بحرية بدون قواعد",
                        "الالتزام بالقوانين والوقت والمسؤوليات في العمل",
                        "العمل حسب التفضيلات فقط",
                        "العمل عند الرغبة",
                        "الالتزام بالقوانين والوقت والمسؤوليات في العمل"
                },
                {
                        "أين تظهر روح المسؤولية في العمل؟",
                        "العمل تحت الإشراف",
                        "إنجاز المهام المكلفة في الوقت المحدد وبجودة",
                        "القيام بالأعمال السهلة فقط",
                        "تجنب المسؤوليات",
                        "إنجاز المهام المكلفة في الوقت المحدد وبجودة"
                },
                {
                        "ما هي فوائد العمل الجماعي؟",
                        "تقليل المسؤولية الفردية",
                        "زيادة كفاءة وجودة العمل",
                        "سهولة التغيب عن العمل",
                        "عدم الحاجة للتواصل",
                        "زيادة كفاءة وجودة العمل"
                },
                {
                        "بماذا تساعد مهارات التواصل في العمل؟",
                        "خلق الصراعات",
                        "سوء الفهم",
                        "تنسيق العمل بفعالية",
                        "العمل بشكل أبطأ",
                        "تنسيق العمل بفعالية"
                },
                {
                        "ما هي أخلاقيات المهنة؟",
                        "قواعد وضعها الأفراد",
                        "معايير السلوك الواجب اتباعها في العمل",
                        "قوانين المرور",
                        "قوانين العائلة",
                        "معايير السلوك الواجب اتباعها في العمل"
                },
                {
                        "الموقف الإيجابي في العمل يظهر من خلال ماذا؟",
                        "تجنب المهام",
                        "كونك استباقياً ومتعاوناً ومسؤولاً",
                        "العمل السطحي",
                        "اتباع الأوامر فقط",
                        "كونك استباقياً ومتعاوناً ومسؤولاً"
                },
                {
                        "ما هي إنتاجية العمل؟",
                        "ساعات العمل",
                        "الكفاءة المحققة في عملية العمل",
                        "الراتب المستلم",
                        "المنصب في الشركة",
                        "الكفاءة المحققة في عملية العمل"
                },
                {
                        "اختيار المهنة يجب أن يعتمد على أي عامل؟",
                        "الاهتمامات والقدرات والاحتياجات الاجتماعية",
                        "آراء الأصدقاء",
                        "المهن الرائجة",
                        "المسافة الجغرافية",
                        "الاهتمامات والقدرات والاحتياجات الاجتماعية"
                },
                {
                        "ما هي المهارات الناعمة في العمل؟",
                        "المهارات المهنية",
                        "مهارات التواصل والعمل الجماعي وإدارة الوقت",
                        "مهارات الآلات",
                        "المهارات اليدوية",
                        "مهارات التواصل والعمل الجماعي وإدارة الوقت"
                },
                {
                        "ما معنى روح التعلم في العمل؟",
                        "غير ضروري",
                        "يساعد على تحسين المؤهلات والتكيف مع التغييرات",
                        "إبطاء العمل",
                        "إضاعة الوقت",
                        "يساعد على تحسين المؤهلات والتكيف مع التغييرات"
                },
                {
                        "كيف تؤثر بيئة العمل على العمال؟",
                        "لا تأثير",
                        "تؤثر على الأداء والروح المعنوية في العمل",
                        "تؤثر على الراتب فقط",
                        "تؤثر على ساعات العمل فقط",
                        "تؤثر على الأداء والروح المعنوية في العمل"
                },
                {
                        "ما هي حقوق العمال؟",
                        "العمل فقط بدون راحة",
                        "الحقوق المضمونة والسلامة المهنية",
                        "لا يُسمح بالشكاوى",
                        "لا يُسمح بالإجازة",
                        "الحقوق المضمونة والسلامة المهنية"
                },
                {
                        "ما هي واجبات العمال؟",
                        "العمل بشكل عشوائي",
                        "الالتزام بالعقود وقوانين العمل",
                        "القيام بما تحب فقط",
                        "لا حاجة للمسؤولية",
                        "الالتزام بالعقود وقوانين العمل"
                },
                {
                        "ماذا يعني التوازن بين العمل والحياة؟",
                        "العمل أكثر",
                        "يساعد على الحفاظ على الصحة والفعالية طويلة المدى",
                        "تقليل الدخل",
                        "العمل أقل",
                        "يساعد على الحفاظ على الصحة والفعالية طويلة المدى"
                },
                {
                        "ما هي البطالة؟",
                        "عدم الرغبة في العمل",
                        "عدم وجود عمل لكن مع الحاجة للعمل",
                        "إجازة طويلة المدى",
                        "العمل الحر",
                        "عدم وجود عمل لكن مع الحاجة للعمل"
                },
                {
                        "ما هو الهدف من التدريب والتطوير المهني؟",
                        "زيادة ساعات العمل",
                        "تحسين المهارات والمؤهلات المهنية",
                        "تقليل المسؤولية",
                        "التسلية",
                        "تحسين المهارات والمؤهلات المهنية"
                },
                {
                        "في رأيك، ما هو أهم عامل للنجاح في العمل؟",
                        "الحظ",
                        "العلاقات",
                        "الجهد والمسؤولية",
                        "المكانة",
                        "الجهد والمسؤولية"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Ý
    private String[][] workItalian(){
        return new String[][] {
                {
                        "Che cos'è il lavoro?",
                        "Attività ricreativa personale",
                        "Attività lavorativa volta a generare reddito o valore",
                        "Attività di apprendimento",
                        "Attività di riposo",
                        "Attività lavorativa volta a generare reddito o valore"
                },
                {
                        "Qual è l'obiettivo principale del lavoro?",
                        "Intrattenimento",
                        "Generare reddito e stabilizzare la vita",
                        "Ammazzare il tempo",
                        "Evitare la disoccupazione",
                        "Generare reddito e stabilizzare la vita"
                },
                {
                        "Quale dei seguenti fattori è più importante per lavorare efficacemente?",
                        "Fortuna",
                        "Competenze e conoscenze",
                        "Età",
                        "Posizione geografica",
                        "Competenze e conoscenze"
                },
                {
                        "Che cos'è la disciplina lavorativa?",
                        "Lavorare liberamente senza regole",
                        "Rispetto di regolamenti, tempo e responsabilità sul lavoro",
                        "Lavorare solo secondo le preferenze",
                        "Lavorare quando se ne ha voglia",
                        "Rispetto di regolamenti, tempo e responsabilità sul lavoro"
                },
                {
                        "Dove si mostra lo spirito di responsabilità nel lavoro?",
                        "Lavorare sotto supervisione",
                        "Completare i compiti assegnati in tempo e con qualità",
                        "Fare solo lavoro facile",
                        "Evitare le responsabilità",
                        "Completare i compiti assegnati in tempo e con qualità"
                },
                {
                        "Quali sono i benefici del lavoro di squadra?",
                        "Ridurre la responsabilità individuale",
                        "Aumentare l'efficienza e la qualità del lavoro",
                        "Facile marinare il lavoro",
                        "Non serve comunicare",
                        "Aumentare l'efficienza e la qualità del lavoro"
                },
                {
                        "A cosa servono le competenze comunicative sul lavoro?",
                        "Creare conflitti",
                        "Incomprensioni",
                        "Coordinare il lavoro efficacemente",
                        "Lavorare più lentamente",
                        "Coordinare il lavoro efficacemente"
                },
                {
                        "Che cos'è l'etica professionale?",
                        "Regole stabilite da individui",
                        "Standard comportamentali da seguire sul lavoro",
                        "Codice della strada",
                        "Regolamenti familiari",
                        "Standard comportamentali da seguire sul lavoro"
                },
                {
                        "L'atteggiamento positivo sul lavoro si mostra attraverso cosa?",
                        "Evitare i compiti",
                        "Essere proattivi, cooperativi e responsabili",
                        "Lavorare superficialmente",
                        "Solo seguire gli ordini",
                        "Essere proattivi, cooperativi e responsabili"
                },
                {
                        "Che cos'è la produttività del lavoro?",
                        "Ore di lavoro",
                        "Efficienza raggiunta nel processo lavorativo",
                        "Stipendio ricevuto",
                        "Posizione nell'azienda",
                        "Efficienza raggiunta nel processo lavorativo"
                },
                {
                        "La scelta della carriera dovrebbe essere basata su quale fattore?",
                        "Interessi, abilità e necessità sociali",
                        "Opinioni degli amici",
                        "Carriere di tendenza",
                        "Distanza geografica",
                        "Interessi, abilità e necessità sociali"
                },
                {
                        "Che cosa sono le competenze trasversali sul lavoro?",
                        "Competenze professionali",
                        "Competenze di comunicazione, lavoro di squadra, gestione del tempo",
                        "Competenze meccaniche",
                        "Competenze manuali",
                        "Competenze di comunicazione, lavoro di squadra, gestione del tempo"
                },
                {
                        "Qual è il significato dello spirito di apprendimento sul lavoro?",
                        "Non necessario",
                        "Aiuta a migliorare le qualifiche e ad adattarsi ai cambiamenti",
                        "Rallentare il lavoro",
                        "Perdere tempo",
                        "Aiuta a migliorare le qualifiche e ad adattarsi ai cambiamenti"
                },
                {
                        "Come influisce l'ambiente di lavoro sui lavoratori?",
                        "Nessun effetto",
                        "Influisce sulle prestazioni lavorative e sullo spirito",
                        "Influisce solo sullo stipendio",
                        "Influisce solo sulle ore di lavoro",
                        "Influisce sulle prestazioni lavorative e sullo spirito"
                },
                {
                        "Quali sono i diritti dei lavoratori?",
                        "Solo lavorare senza riposo",
                        "Diritti garantiti e sicurezza sul lavoro",
                        "Non sono ammessi reclami",
                        "Non sono ammessi congedi",
                        "Diritti garantiti e sicurezza sul lavoro"
                },
                {
                        "Quali sono gli obblighi dei lavoratori?",
                        "Lavorare arbitrariamente",
                        "Rispettare contratti e regolamenti lavorativi",
                        "Fare solo quello che piace",
                        "Non serve responsabilità",
                        "Rispettare contratti e regolamenti lavorativi"
                },
                {
                        "Cosa significa equilibrio vita-lavoro?",
                        "Lavorare di più",
                        "Aiuta a mantenere salute ed efficacia a lungo termine",
                        "Ridurre il reddito",
                        "Lavorare di meno",
                        "Aiuta a mantenere salute ed efficacia a lungo termine"
                },
                {
                        "Che cos'è la disoccupazione?",
                        "Non voler lavorare",
                        "Nessun lavoro ma bisogno di lavorare",
                        "Congedo a lungo termine",
                        "Lavoro freelance",
                        "Nessun lavoro ma bisogno di lavorare"
                },
                {
                        "Qual è lo scopo della formazione e sviluppo professionale?",
                        "Aumentare le ore di lavoro",
                        "Migliorare competenze e qualifiche professionali",
                        "Ridurre la responsabilità",
                        "Intrattenimento",
                        "Migliorare competenze e qualifiche professionali"
                },
                {
                        "A tuo parere, qual è il fattore più importante per il successo sul lavoro?",
                        "Fortuna",
                        "Relazioni",
                        "Impegno e responsabilità",
                        "Status",
                        "Impegno e responsabilità"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Thổ Nhĩ Kỳ
    private String[][] workTurkish(){
        return new String[][] {
                {
                        "Çalışma nedir?",
                        "Kişisel eğlence etkinliği",
                        "Gelir veya değer üretmeyi amaçlayan emek etkinliği",
                        "Öğrenme etkinliği",
                        "Dinlenme etkinliği",
                        "Gelir veya değer üretmeyi amaçlayan emek etkinliği"
                },
                {
                        "Çalışmanın temel amacı nedir?",
                        "Eğlence",
                        "Gelir üretmek ve hayatı istikrara kavuşturmak",
                        "Zaman öldürmek",
                        "İşsizlikten kaçınmak",
                        "Gelir üretmek ve hayatı istikrara kavuşturmak"
                },
                {
                        "Etkili çalışmak için aşağıdaki faktörlerden hangisi en önemlidir?",
                        "Şans",
                        "Beceriler ve bilgi",
                        "Yaş",
                        "Coğrafi konum",
                        "Beceriler ve bilgi"
                },
                {
                        "Çalışma disiplini nedir?",
                        "Kuralsız özgürce çalışmak",
                        "İş yerinde kurallar, zaman ve sorumluluklara uyum",
                        "Sadece tercihlere göre çalışmak",
                        "Canı istediğinde çalışmak",
                        "İş yerinde kurallar, zaman ve sorumluluklara uyum"
                },
                {
                        "İşteki sorumluluk ruhu nerede gösterilir?",
                        "Gözetim altında çalışırken",
                        "Verilen görevleri zamanında ve kaliteli tamamlamak",
                        "Sadece kolay iş yapmak",
                        "Sorumluluktan kaçmak",
                        "Verilen görevleri zamanında ve kaliteli tamamlamak"
                },
                {
                        "Takım çalışmasının faydaları nelerdir?",
                        "Bireysel sorumluluğu azaltmak",
                        "İş verimliliği ve kalitesini artırmak",
                        "İşten kaçmak kolay",
                        "İletişime gerek yok",
                        "İş verimliliği ve kalitesini artırmak"
                },
                {
                        "İşteki iletişim becerileri neye yardımcı olur?",
                        "Çatışma yaratmak",
                        "Yanlış anlama",
                        "İşi etkili bir şekilde koordine etmek",
                        "Daha yavaş çalışmak",
                        "İşi etkili bir şekilde koordine etmek"
                },
                {
                        "Meslek etiği nedir?",
                        "Bireyler tarafından belirlenen kurallar",
                        "İş yerinde uyulması gereken davranış standartları",
                        "Trafik kuralları",
                        "Aile kuralları",
                        "İş yerinde uyulması gereken davranış standartları"
                },
                {
                        "Pozitif iş tutumu neyle gösterilir?",
                        "Görevlerden kaçmak",
                        "Proaktif, işbirlikçi ve sorumlu olmak",
                        "Yüzeysel çalışmak",
                        "Sadece emirleri takip etmek",
                        "Proaktif, işbirlikçi ve sorumlu olmak"
                },
                {
                        "Emek verimliliği nedir?",
                        "Çalışma saatleri",
                        "Emek sürecinde elde edilen verimlilik",
                        "Alınan maaş",
                        "Şirketteki pozisyon",
                        "Emek sürecinde elde edilen verimlilik"
                },
                {
                        "Kariyer seçimi hangi faktöre dayanmalıdır?",
                        "İlgiler, yetenekler ve sosyal ihtiyaçlar",
                        "Arkadaşların görüşleri",
                        "Trend kariyerler",
                        "Coğrafi mesafe",
                        "İlgiler, yetenekler ve sosyal ihtiyaçlar"
                },
                {
                        "İşteki yumuşak beceriler nelerdir?",
                        "Profesyonel beceriler",
                        "İletişim, takım çalışması, zaman yönetimi becerileri",
                        "Makine becerileri",
                        "El becerileri",
                        "İletişim, takım çalışması, zaman yönetimi becerileri"
                },
                {
                        "İşteki öğrenme ruhunun anlamı nedir?",
                        "Gerekli değil",
                        "Nitelikleri geliştirmeye ve değişikliklere uyum sağlamaya yardımcı olur",
                        "İşi yavaşlatmak",
                        "Zaman kaybetmek",
                        "Nitelikleri geliştirmeye ve değişikliklere uyum sağlamaya yardımcı olur"
                },
                {
                        "Çalışma ortamı işçileri nasıl etkiler?",
                        "Hiçbir etkisi yok",
                        "İş performansını ve ruhunu etkiler",
                        "Sadece maaşı etkiler",
                        "Sadece çalışma saatlerini etkiler",
                        "İş performansını ve ruhunu etkiler"
                },
                {
                        "İşçilerin hakları nelerdir?",
                        "Sadece dinlenmeden çalışmak",
                        "Garanti edilen haklar ve iş güvenliği",
                        "Şikayet yapılamaz",
                        "İzin alınamaz",
                        "Garanti edilen haklar ve iş güvenliği"
                },
                {
                        "İşçilerin yükümlülükleri nelerdir?",
                        "Keyfi çalışmak",
                        "Sözleşme ve iş kurallarına uymak",
                        "Sadece sevdiğini yapmak",
                        "Sorumluluk gerekmez",
                        "Sözleşme ve iş kurallarına uymak"
                },
                {
                        "İş-yaşam dengesi ne anlama gelir?",
                        "Daha çok çalışmak",
                        "Sağlık ve uzun vadeli etkinliği korumaya yardımcı olur",
                        "Geliri azaltmak",
                        "Daha az çalışmak",
                        "Sağlık ve uzun vadeli etkinliği korumaya yardımcı olur"
                },
                {
                        "İşsizlik nedir?",
                        "Çalışmak istememek",
                        "İş yok ama çalışma ihtiyacı var",
                        "Uzun vadeli izin",
                        "Serbest meslek",
                        "İş yok ama çalışma ihtiyacı var"
                },
                {
                        "Mesleki eğitim ve gelişimin amacı nedir?",
                        "Çalışma saatlerini artırmak",
                        "Becerileri ve mesleki nitelikleri geliştirmek",
                        "Sorumluluğu azaltmak",
                        "Eğlence",
                        "Becerileri ve mesleki nitelikleri geliştirmek"
                },
                {
                        "Sizce işte başarı için en önemli faktör nedir?",
                        "Şans",
                        "İlişkiler",
                        "Çaba ve sorumluluk",
                        "Statü",
                        "Çaba ve sorumluluk"
                }
        };
    }

    // Hàm trả về câu hỏi tiếng Thái
    private String[][] workThai(){
        return new String[][] {
                {
                        "งานคืออะไร?",
                        "กิจกรรมความบันเทิงส่วนบุคคล",
                        "กิจกรรมแรงงานที่มุ่งสร้างรายได้หรือคุณค่า",
                        "กิจกรรมการเรียนรู้",
                        "กิจกรรมพักผ่อน",
                        "กิจกรรมแรงงานที่มุ่งสร้างรายได้หรือคุณค่า"
                },
                {
                        "วัตถุประสงค์หลักของงานคืออะไร?",
                        "ความบันเทิง",
                        "สร้างรายได้และทำให้ชีวิตมีเสถียรภาพ",
                        "ฆ่าเวลา",
                        "หลีกเลี่ยงการว่างงาน",
                        "สร้างรายได้และทำให้ชีวิตมีเสถียรภาพ"
                },
                {
                        "ปัจจัยใดต่อไปนี้ที่สำคัญที่สุดสำหรับการทำงานอย่างมีประสิทธิภาพ?",
                        "โชค",
                        "ทักษะและความรู้",
                        "อายุ",
                        "ตำแหน่งทางภูมิศาสตร์",
                        "ทักษะและความรู้"
                },
                {
                        "ระเบียบวินัยในการทำงานคืออะไร?",
                        "ทำงานอย่างเสรีโดยไม่มีกฎเกณฑ์",
                        "การปฏิบัติตามกฎระเบียบ เวลา และความรับผิดชอบในงาน",
                        "ทำงานตามความชอบเท่านั้น",
                        "ทำงานเมื่ออยากทำ",
                        "การปฏิบัติตามกฎระเบียบ เวลา และความรับผิดชอบในงาน"
                },
                {
                        "จิตใจความรับผิดชอบในงานแสดงออกที่ไหน?",
                        "ทำงานเมื่อมีการดูแล",
                        "ทำงานที่ได้รับมอบหมายให้เสร็จทันเวลาและมีคุณภาพ",
                        "ทำเฉพาะงานง่าย",
                        "หลบหลีกความรับผิดชอบ",
                        "ทำงานที่ได้รับมอบหมายให้เสร็จทันเวลาและมีคุณภาพ"
                },
                {
                        "การทำงานเป็นทีมมีประโยชน์อย่างไร?",
                        "ลดความรับผิดชอบส่วนบุคคล",
                        "เพิ่มประสิทธิภาพและคุณภาพของงาน",
                        "หลบงานได้ง่าย",
                        "ไม่ต้องสื่อสาร",
                        "เพิ่มประสิทธิภาพและคุณภาพของงาน"
                },
                {
                        "ทักษะการสื่อสารในงานช่วยอะไร?",
                        "สร้างความขัดแย้ง",
                        "ความเข้าใจผิด",
                        "ประสานงานอย่างมีประสิทธิภาพ",
                        "ทำงานช้าลง",
                        "ประสานงานอย่างมีประสิทธิภาพ"
                },
                {
                        "จรรยาบรรณวิชาชีพคืออะไร?",
                        "กฎเกณฑ์ที่บุคคลกำหนด",
                        "มาตรฐานพฤติกรรมที่ต้องปฏิบัติในงาน",
                        "กฎจราจร",
                        "กฎของครอบครัว",
                        "มาตรฐานพฤติกรรมที่ต้องปฏิบัติในงาน"
                },
                {
                        "ทัศนคติเชิงบวกในงานแสดงออกผ่านอะไร?",
                        "หลีกเลี่ยงงาน",
                        "เป็นคนเชิงรุก ร่วมมือ และรับผิดชอบ",
                        "ทำงานแบบผิวเผิน",
                        "ทำตามคำสั่งเท่านั้น",
                        "เป็นคนเชิงรุก ร่วมมือ และรับผิดชอบ"
                },
                {
                        "ผลิตภาพแรงงานคืออะไร?",
                        "ชั่วโมงทำงาน",
                        "ประสิทธิภาพที่ได้รับในกระบวนการแรงงาน",
                        "เงินเดือนที่ได้รับ",
                        "ตำแหน่งในบริษัท",
                        "ประสิทธิภาพที่ได้รับในกระบวนการแรงงาน"
                },
                {
                        "การเลือกอาชีพควรอิงจากปัจจัยใด?",
                        "ความสนใจ ความสามารถ และความต้องการของสังคม",
                        "ความคิดเห็นของเพื่อน",
                        "อาชีพที่กำลังฮิต",
                        "ระยะทางทางภูมิศาสตร์",
                        "ความสนใจ ความสามารถ และความต้องการของสังคม"
                },
                {
                        "ทักษะอ่อนในงานคืออะไร?",
                        "ทักษะเชิงวิชาชีพ",
                        "ทักษะการสื่อสาร การทำงานเป็นทีม การบริหารเวลา",
                        "ทักษะเครื่องจักร",
                        "ทักษะงานใช้มือ",
                        "ทักษะการสื่อสาร การทำงานเป็นทีม การบริหารเวลา"
                },
                {
                        "จิตใจในการเรียนรู้ในงานมีความหมายอย่างไร?",
                        "ไม่จำเป็น",
                        "ช่วยยกระดับคุณวุฒิและปรับตัวกับการเปลี่ยนแปลง",
                        "ทำให้งานช้าลง",
                        "เสียเวลา",
                        "ช่วยยกระดับคุณวุฒิและปรับตัวกับการเปลี่ยนแปลง"
                },
                {
                        "สภาพแวดล้อมในการทำงานส่งผลต่อคนงานอย่างไร?",
                        "ไม่มีผลกระทบ",
                        "ส่งผลต่อประสิทธิภาพและจิตใจในการทำงาน",
                        "ส่งผลต่อเงินเดือนเท่านั้น",
                        "ส่งผลต่อชั่วโมงทำงานเท่านั้น",
                        "ส่งผลต่อประสิทธิภาพและจิตใจในการทำงาน"
                },
                {
                        "สิทธิของคนงานคืออะไร?",
                        "ทำงานอย่างเดียวโดยไม่ได้พัก",
                        "สิทธิที่รับประกันและความปลอดภัยในการทำงาน",
                        "ไม่ได้รับอนุญาตให้ร้องเรียน",
                        "ไม่ได้รับอนุญาตให้ลา",
                        "สิทธิที่รับประกันและความปลอดภัยในการทำงาน"
                },
                {
                        "หน้าที่ของคนงานคืออะไร?",
                        "ทำงานตามอำเภอใจ",
                        "ปฏิบัติตามสัญญาและระเบียบการทำงาน",
                        "ทำเฉพาะสิ่งที่ชอบ",
                        "ไม่ต้องรับผิดชอบ",
                        "ปฏิบัติตามสัญญาและระเบียบการทำงาน"
                },
                {
                        "การสมดุลระหว่างงานและชีวิตหมายความว่าอย่างไร?",
                        "ทำงานมากขึ้น",
                        "ช่วยรักษาสุขภาพและประสิทธิภาพระยะยาว",
                        "ลดรายได้",
                        "ทำงานน้อยลง",
                        "ช่วยรักษาสุขภาพและประสิทธิภาพระยะยาว"
                },
                {
                        "การว่างงานคืออะไร?",
                        "ไม่อยากทำงาน",
                        "ไม่มีงาน แต่มีความต้องการทำงาน",
                        "ลายาวระยะยาว",
                        "งานฟรีแลนซ์",
                        "ไม่มีงาน แต่มีความต้องการทำงาน"
                },
                {
                        "วัตถุประสงค์ของการฝึกอบรมและพัฒนาวิชาชีพคืออะไร?",
                        "เพิ่มชั่วโมงทำงาน",
                        "ยกระดับทักษะและคุณวุฒิทางวิชาชีพ",
                        "ลดความรับผิดชอบ",
                        "ความบันเทิง",
                        "ยกระดับทักษะและคุณวุฒิทางวิชาชีพ"
                },
                {
                        "ในความคิดเห็นของคุณ ปัจจัยที่สำคัญที่สุดสำหรับความสำเร็จในงานคืออะไร?",
                        "โชค",
                        "ความสัมพันธ์",
                        "ความพยายามและความรับผิดชอบ",
                        "สถานะ",
                        "ความพยายามและความรับผิดชอบ"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Anh
    private String[][] educationEnglish(){
        return new String[][] {
                {
                        "What is education?",
                        "Entertainment activity",
                        "Process of transmitting knowledge and skills",
                        "Business activity",
                        "Resting activity",
                        "Process of transmitting knowledge and skills"
                },
                {
                        "What is the main goal of education?",
                        "Creating pressure",
                        "Comprehensive human development",
                        "Only transmitting knowledge",
                        "Only training discipline",
                        "Comprehensive human development"
                },
                {
                        "What does education help humans develop?",
                        "Physical aspects",
                        "Intelligence",
                        "Morality",
                        "All of the above factors",
                        "All of the above factors"
                },
                {
                        "Where does education begin?",
                        "School",
                        "Family",
                        "Society",
                        "Family and school",
                        "Family and school"
                },
                {
                        "What is the meaning of lifelong learning?",
                        "Not necessary",
                        "Helps adapt to social changes",
                        "Only for students",
                        "Waste of time",
                        "Helps adapt to social changes"
                },
                {
                        "What is the role of teachers?",
                        "Transmitting knowledge and guidance",
                        "Only grading",
                        "Managing students",
                        "Giving assignments",
                        "Transmitting knowledge and guidance"
                },
                {
                        "What is the role of students in learning?",
                        "Passive listening",
                        "Active learning",
                        "Only studying for exams",
                        "Depending on teachers",
                        "Active learning"
                },
                {
                        "What is the purpose of moral education?",
                        "Controlling behavior",
                        "Character formation",
                        "Creating pressure",
                        "Only teaching etiquette",
                        "Character formation"
                },
                {
                        "What does life skills education help students with?",
                        "Entertainment",
                        "Dealing with real-life situations",
                        "Only theoretical learning",
                        "Not necessary",
                        "Dealing with real-life situations"
                },
                {
                        "How does the educational environment influence?",
                        "No influence",
                        "Affects learning outcomes",
                        "Only affects teachers",
                        "Only affects infrastructure",
                        "Affects learning outcomes"
                },
                {
                        "What role does self-study play?",
                        "Not necessary",
                        "Helps improve knowledge and skills",
                        "Only for smart people",
                        "Creates pressure",
                        "Helps improve knowledge and skills"
                },
                {
                        "What does civic education help form?",
                        "Sense of social responsibility",
                        "Only legal knowledge",
                        "Professional skills",
                        "Physical strength",
                        "Sense of social responsibility"
                },
                {
                        "What does 'learning goes hand in hand with practice' mean?",
                        "Only theoretical learning",
                        "Combining theory with practice",
                        "Only practice",
                        "No need to study",
                        "Combining theory with practice"
                },
                {
                        "What does educational equality mean?",
                        "Everyone has access to education",
                        "Only smart people can study",
                        "Only rich people can study",
                        "No gender discrimination",
                        "Everyone has access to education"
                },
                {
                        "What role does education play for society?",
                        "Not important",
                        "Developing human resources",
                        "Only serving individuals",
                        "Only serving economy",
                        "Developing human resources"
                },
                {
                        "What does learning help humans with?",
                        "Increase income",
                        "Expand understanding",
                        "Personal development",
                        "All of the above",
                        "All of the above"
                },
                {
                        "What does modern education focus on?",
                        "Memorization",
                        "Creative thinking",
                        "Testing",
                        "Rigid discipline",
                        "Creative thinking"
                },
                {
                        "What does technology in education help with?",
                        "Causes distraction",
                        "Improves learning effectiveness",
                        "Reduces quality",
                        "Not necessary",
                        "Improves learning effectiveness"
                },
                {
                        "What is students' responsibility in learning?",
                        "Superficial studying",
                        "Proactive and conscious",
                        "Only listening to lectures",
                        "Depending on others",
                        "Proactive and conscious"
                },
                {
                        "What is the most important role of education?",
                        "Transmitting knowledge",
                        "Forming well-rounded individuals",
                        "Testing",
                        "Ranking",
                        "Forming well-rounded individuals"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Việt (dữ liệu gốc)
    private String[][] educationVN(){
        return new String[][] {
                {
                        "Giáo dục là gì?",
                        "Hoạt động giải trí",
                        "Quá trình truyền đạt tri thức và kỹ năng",
                        "Hoạt động kinh doanh",
                        "Hoạt động nghỉ ngơi",
                        "Quá trình truyền đạt tri thức và kỹ năng"
                },
                {
                        "Mục tiêu chính của giáo dục là gì?",
                        "Tạo áp lực",
                        "Phát triển con người toàn diện",
                        "Chỉ truyền kiến thức",
                        "Chỉ rèn kỷ luật",
                        "Phát triển con người toàn diện"
                },
                {
                        "Giáo dục giúp con người phát triển điều gì?",
                        "Thể chất",
                        "Trí tuệ",
                        "Đạo đức",
                        "Tất cả các yếu tố trên",
                        "Tất cả các yếu tố trên"
                },
                {
                        "Giáo dục bắt đầu từ đâu?",
                        "Nhà trường",
                        "Gia đình",
                        "Xã hội",
                        "Gia đình và nhà trường",
                        "Gia đình và nhà trường"
                },
                {
                        "Học tập suốt đời có ý nghĩa gì?",
                        "Không cần thiết",
                        "Giúp thích nghi với thay đổi xã hội",
                        "Chỉ dành cho học sinh",
                        "Tốn thời gian",
                        "Giúp thích nghi với thay đổi xã hội"
                },
                {
                        "Vai trò của giáo viên là gì?",
                        "Truyền đạt kiến thức và định hướng",
                        "Chỉ chấm điểm",
                        "Quản lý học sinh",
                        "Ra bài tập",
                        "Truyền đạt kiến thức và định hướng"
                },
                {
                        "Vai trò của học sinh trong học tập là gì?",
                        "Thụ động nghe giảng",
                        "Chủ động học tập",
                        "Chỉ học để thi",
                        "Phụ thuộc giáo viên",
                        "Chủ động học tập"
                },
                {
                        "Giáo dục đạo đức nhằm mục đích gì?",
                        "Kiểm soát hành vi",
                        "Hình thành nhân cách",
                        "Tạo áp lực",
                        "Chỉ dạy lễ nghi",
                        "Hình thành nhân cách"
                },
                {
                        "Giáo dục kỹ năng sống giúp gì cho học sinh?",
                        "Giải trí",
                        "Ứng phó với tình huống thực tế",
                        "Chỉ học lý thuyết",
                        "Không cần thiết",
                        "Ứng phó với tình huống thực tế"
                },
                {
                        "Môi trường giáo dục ảnh hưởng như thế nào?",
                        "Không ảnh hưởng",
                        "Ảnh hưởng đến kết quả học tập",
                        "Chỉ ảnh hưởng giáo viên",
                        "Chỉ ảnh hưởng cơ sở vật chất",
                        "Ảnh hưởng đến kết quả học tập"
                },
                {
                        "Tự học có vai trò gì?",
                        "Không cần thiết",
                        "Giúp nâng cao kiến thức và kỹ năng",
                        "Chỉ dành cho người giỏi",
                        "Gây áp lực",
                        "Giúp nâng cao kiến thức và kỹ năng"
                },
                {
                        "Giáo dục công dân giúp hình thành điều gì?",
                        "Ý thức trách nhiệm xã hội",
                        "Chỉ kiến thức pháp luật",
                        "Kỹ năng nghề",
                        "Thể chất",
                        "Ý thức trách nhiệm xã hội"
                },
                {
                        "Học đi đôi với hành nghĩa là gì?",
                        "Chỉ học lý thuyết",
                        "Kết hợp lý thuyết với thực hành",
                        "Chỉ thực hành",
                        "Không cần học",
                        "Kết hợp lý thuyết với thực hành"
                },
                {
                        "Giáo dục bình đẳng nghĩa là gì?",
                        "Mọi người đều được tiếp cận giáo dục",
                        "Chỉ người giỏi được học",
                        "Chỉ người giàu được học",
                        "Không phân biệt giới tính",
                        "Mọi người đều được tiếp cận giáo dục"
                },
                {
                        "Giáo dục có vai trò gì đối với xã hội?",
                        "Không quan trọng",
                        "Phát triển nguồn nhân lực",
                        "Chỉ phục vụ cá nhân",
                        "Chỉ phục vụ kinh tế",
                        "Phát triển nguồn nhân lực"
                },
                {
                        "Học tập giúp con người điều gì?",
                        "Tăng thu nhập",
                        "Mở rộng hiểu biết",
                        "Phát triển bản thân",
                        "Tất cả các ý trên",
                        "Tất cả các ý trên"
                },
                {
                        "Giáo dục hiện đại chú trọng điều gì?",
                        "Học thuộc lòng",
                        "Tư duy sáng tạo",
                        "Thi cử",
                        "Kỷ luật cứng nhắc",
                        "Tư duy sáng tạo"
                },
                {
                        "Công nghệ trong giáo dục giúp gì?",
                        "Gây xao nhãng",
                        "Nâng cao hiệu quả học tập",
                        "Làm giảm chất lượng",
                        "Không cần thiết",
                        "Nâng cao hiệu quả học tập"
                },
                {
                        "Trách nhiệm học tập của học sinh là gì?",
                        "Học đối phó",
                        "Chủ động và tự giác",
                        "Chỉ nghe giảng",
                        "Phụ thuộc người khác",
                        "Chủ động và tự giác"
                },
                {
                        "Vai trò quan trọng nhất của giáo dục là gì?",
                        "Truyền kiến thức",
                        "Hình thành con người toàn diện",
                        "Thi cử",
                        "Xếp hạng",
                        "Hình thành con người toàn diện"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Tây Ban Nha
    private String[][] educationSpanish(){
        return new String[][] {
                {
                        "¿Qué es la educación?",
                        "Actividad de entretenimiento",
                        "Proceso de transmisión de conocimiento y habilidades",
                        "Actividad comercial",
                        "Actividad de descanso",
                        "Proceso de transmisión de conocimiento y habilidades"
                },
                {
                        "¿Cuál es el objetivo principal de la educación?",
                        "Crear presión",
                        "Desarrollo integral del ser humano",
                        "Solo transmitir conocimiento",
                        "Solo entrenar disciplina",
                        "Desarrollo integral del ser humano"
                },
                {
                        "¿Qué ayuda a desarrollar la educación en los humanos?",
                        "Aspectos físicos",
                        "Inteligencia",
                        "Moralidad",
                        "Todos los factores anteriores",
                        "Todos los factores anteriores"
                },
                {
                        "¿Dónde comienza la educación?",
                        "Escuela",
                        "Familia",
                        "Sociedad",
                        "Familia y escuela",
                        "Familia y escuela"
                },
                {
                        "¿Cuál es el significado del aprendizaje permanente?",
                        "No es necesario",
                        "Ayuda a adaptarse a los cambios sociales",
                        "Solo para estudiantes",
                        "Pérdida de tiempo",
                        "Ayuda a adaptarse a los cambios sociales"
                },
                {
                        "¿Cuál es el papel de los profesores?",
                        "Transmitir conocimiento y orientación",
                        "Solo calificar",
                        "Gestionar estudiantes",
                        "Dar tareas",
                        "Transmitir conocimiento y orientación"
                },
                {
                        "¿Cuál es el papel de los estudiantes en el aprendizaje?",
                        "Escuchar pasivamente",
                        "Aprendizaje activo",
                        "Solo estudiar para exámenes",
                        "Depender de profesores",
                        "Aprendizaje activo"
                },
                {
                        "¿Cuál es el propósito de la educación moral?",
                        "Controlar comportamiento",
                        "Formación del carácter",
                        "Crear presión",
                        "Solo enseñar etiqueta",
                        "Formación del carácter"
                },
                {
                        "¿En qué ayuda la educación de habilidades para la vida a los estudiantes?",
                        "Entretenimiento",
                        "Enfrentar situaciones de la vida real",
                        "Solo aprendizaje teórico",
                        "No es necesario",
                        "Enfrentar situaciones de la vida real"
                },
                {
                        "¿Cómo influye el ambiente educativo?",
                        "No influye",
                        "Afecta los resultados del aprendizaje",
                        "Solo afecta a los profesores",
                        "Solo afecta la infraestructura",
                        "Afecta los resultados del aprendizaje"
                },
                {
                        "¿Qué papel juega el autoestudio?",
                        "No es necesario",
                        "Ayuda a mejorar conocimiento y habilidades",
                        "Solo para personas inteligentes",
                        "Crea presión",
                        "Ayuda a mejorar conocimiento y habilidades"
                },
                {
                        "¿Qué ayuda a formar la educación cívica?",
                        "Sentido de responsabilidad social",
                        "Solo conocimiento legal",
                        "Habilidades profesionales",
                        "Fuerza física",
                        "Sentido de responsabilidad social"
                },
                {
                        "¿Qué significa 'el aprendizaje va de la mano con la práctica'?",
                        "Solo aprendizaje teórico",
                        "Combinar teoría con práctica",
                        "Solo práctica",
                        "No necesidad de estudiar",
                        "Combinar teoría con práctica"
                },
                {
                        "¿Qué significa igualdad educativa?",
                        "Todos tienen acceso a la educación",
                        "Solo las personas inteligentes pueden estudiar",
                        "Solo las personas ricas pueden estudiar",
                        "Sin discriminación de género",
                        "Todos tienen acceso a la educación"
                },
                {
                        "¿Qué papel juega la educación para la sociedad?",
                        "No es importante",
                        "Desarrollo de recursos humanos",
                        "Solo sirve a individuos",
                        "Solo sirve a la economía",
                        "Desarrollo de recursos humanos"
                },
                {
                        "¿En qué ayuda el aprendizaje a los humanos?",
                        "Aumentar ingresos",
                        "Expandir comprensión",
                        "Desarrollo personal",
                        "Todo lo anterior",
                        "Todo lo anterior"
                },
                {
                        "¿En qué se enfoca la educación moderna?",
                        "Memorización",
                        "Pensamiento creativo",
                        "Exámenes",
                        "Disciplina rígida",
                        "Pensamiento creativo"
                },
                {
                        "¿En qué ayuda la tecnología en la educación?",
                        "Causa distracción",
                        "Mejora la efectividad del aprendizaje",
                        "Reduce la calidad",
                        "No es necesario",
                        "Mejora la efectividad del aprendizaje"
                },
                {
                        "¿Cuál es la responsabilidad de los estudiantes en el aprendizaje?",
                        "Estudio superficial",
                        "Proactivo y consciente",
                        "Solo escuchar conferencias",
                        "Depender de otros",
                        "Proactivo y consciente"
                },
                {
                        "¿Cuál es el papel más importante de la educación?",
                        "Transmitir conocimiento",
                        "Formar individuos integrales",
                        "Examinar",
                        "Clasificar",
                        "Formar individuos integrales"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Pháp
    private String[][] educationFrench(){
        return new String[][] {
                {
                        "Qu'est-ce que l'éducation?",
                        "Activité de divertissement",
                        "Processus de transmission de connaissances et de compétences",
                        "Activité commerciale",
                        "Activité de repos",
                        "Processus de transmission de connaissances et de compétences"
                },
                {
                        "Quel est l'objectif principal de l'éducation?",
                        "Créer de la pression",
                        "Développement humain complet",
                        "Seulement transmettre des connaissances",
                        "Seulement entraîner la discipline",
                        "Développement humain complet"
                },
                {
                        "Qu'est-ce que l'éducation aide les humains à développer?",
                        "Aspects physiques",
                        "Intelligence",
                        "Moralité",
                        "Tous les facteurs ci-dessus",
                        "Tous les facteurs ci-dessus"
                },
                {
                        "Où commence l'éducation?",
                        "École",
                        "Famille",
                        "Société",
                        "Famille et école",
                        "Famille et école"
                },
                {
                        "Quelle est la signification de l'apprentissage tout au long de la vie?",
                        "Pas nécessaire",
                        "Aide à s'adapter aux changements sociaux",
                        "Seulement pour les étudiants",
                        "Perte de temps",
                        "Aide à s'adapter aux changements sociaux"
                },
                {
                        "Quel est le rôle des enseignants?",
                        "Transmettre des connaissances et orienter",
                        "Seulement noter",
                        "Gérer les étudiants",
                        "Donner des devoirs",
                        "Transmettre des connaissances et orienter"
                },
                {
                        "Quel est le rôle des étudiants dans l'apprentissage?",
                        "Écoute passive",
                        "Apprentissage actif",
                        "Étudier seulement pour les examens",
                        "Dépendre des enseignants",
                        "Apprentissage actif"
                },
                {
                        "Quel est le but de l'éducation morale?",
                        "Contrôler le comportement",
                        "Formation du caractère",
                        "Créer de la pression",
                        "Enseigner seulement l'étiquette",
                        "Formation du caractère"
                },
                {
                        "En quoi l'éducation aux compétences de vie aide-t-elle les étudiants?",
                        "Divertissement",
                        "Faire face aux situations réelles",
                        "Apprentissage théorique seulement",
                        "Pas nécessaire",
                        "Faire face aux situations réelles"
                },
                {
                        "Comment l'environnement éducatif influence-t-il?",
                        "Aucune influence",
                        "Affecte les résultats d'apprentissage",
                        "Affecte seulement les enseignants",
                        "Affecte seulement l'infrastructure",
                        "Affecte les résultats d'apprentissage"
                },
                {
                        "Quel rôle joue l'auto-apprentissage?",
                        "Pas nécessaire",
                        "Aide à améliorer les connaissances et compétences",
                        "Seulement pour les personnes intelligentes",
                        "Crée de la pression",
                        "Aide à améliorer les connaissances et compétences"
                },
                {
                        "Qu'est-ce que l'éducation civique aide à former?",
                        "Sens de la responsabilité sociale",
                        "Connaissances juridiques seulement",
                        "Compétences professionnelles",
                        "Force physique",
                        "Sens de la responsabilité sociale"
                },
                {
                        "Que signifie 'l'apprentissage va de pair avec la pratique'?",
                        "Apprentissage théorique seulement",
                        "Combiner théorie et pratique",
                        "Pratique seulement",
                        "Pas besoin d'étudier",
                        "Combiner théorie et pratique"
                },
                {
                        "Que signifie l'égalité éducative?",
                        "Tout le monde a accès à l'éducation",
                        "Seules les personnes intelligentes peuvent étudier",
                        "Seules les personnes riches peuvent étudier",
                        "Pas de discrimination de genre",
                        "Tout le monde a accès à l'éducation"
                },
                {
                        "Quel rôle l'éducation joue-t-elle pour la société?",
                        "Pas important",
                        "Développement des ressources humaines",
                        "Sert seulement les individus",
                        "Sert seulement l'économie",
                        "Développement des ressources humaines"
                },
                {
                        "En quoi l'apprentissage aide-t-il les humains?",
                        "Augmenter les revenus",
                        "Élargir la compréhension",
                        "Développement personnel",
                        "Tout ce qui précède",
                        "Tout ce qui précède"
                },
                {
                        "Sur quoi se concentre l'éducation moderne?",
                        "Mémorisation",
                        "Pensée créative",
                        "Examens",
                        "Discipline rigide",
                        "Pensée créative"
                },
                {
                        "En quoi la technologie dans l'éducation aide-t-elle?",
                        "Cause de la distraction",
                        "Améliore l'efficacité de l'apprentissage",
                        "Réduit la qualité",
                        "Pas nécessaire",
                        "Améliore l'efficacité de l'apprentissage"
                },
                {
                        "Quelle est la responsabilité des étudiants dans l'apprentissage?",
                        "Étude superficielle",
                        "Proactif et conscient",
                        "Écouter seulement les cours",
                        "Dépendre des autres",
                        "Proactif et conscient"
                },
                {
                        "Quel est le rôle le plus important de l'éducation?",
                        "Transmettre des connaissances",
                        "Former des individus complets",
                        "Examiner",
                        "Classer",
                        "Former des individus complets"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Đức
    private String[][] educationGerman(){
        return new String[][] {
                {
                        "Was ist Bildung?",
                        "Unterhaltungsaktivität",
                        "Prozess der Übertragung von Wissen und Fähigkeiten",
                        "Geschäftstätigkeit",
                        "Ruheaktivität",
                        "Prozess der Übertragung von Wissen und Fähigkeiten"
                },
                {
                        "Was ist das Hauptziel der Bildung?",
                        "Druck erzeugen",
                        "Umfassende menschliche Entwicklung",
                        "Nur Wissen übertragen",
                        "Nur Disziplin trainieren",
                        "Umfassende menschliche Entwicklung"
                },
                {
                        "Was hilft Bildung Menschen zu entwickeln?",
                        "Körperliche Aspekte",
                        "Intelligenz",
                        "Moral",
                        "Alle oben genannten Faktoren",
                        "Alle oben genannten Faktoren"
                },
                {
                        "Wo beginnt Bildung?",
                        "Schule",
                        "Familie",
                        "Gesellschaft",
                        "Familie und Schule",
                        "Familie und Schule"
                },
                {
                        "Was bedeutet lebenslanges Lernen?",
                        "Nicht notwendig",
                        "Hilft bei der Anpassung an gesellschaftliche Veränderungen",
                        "Nur für Schüler",
                        "Zeitverschwendung",
                        "Hilft bei der Anpassung an gesellschaftliche Veränderungen"
                },
                {
                        "Was ist die Rolle der Lehrer?",
                        "Wissen übertragen und orientieren",
                        "Nur bewerten",
                        "Schüler verwalten",
                        "Aufgaben geben",
                        "Wissen übertragen und orientieren"
                },
                {
                        "Was ist die Rolle der Schüler beim Lernen?",
                        "Passives Zuhören",
                        "Aktives Lernen",
                        "Nur für Prüfungen lernen",
                        "Von Lehrern abhängen",
                        "Aktives Lernen"
                },
                {
                        "Was ist der Zweck der Moralerziehung?",
                        "Verhalten kontrollieren",
                        "Charakterbildung",
                        "Druck erzeugen",
                        "Nur Etikette lehren",
                        "Charakterbildung"
                },
                {
                        "Wobei hilft Lebensfertigkeiten-Bildung den Schülern?",
                        "Unterhaltung",
                        "Umgang mit realen Situationen",
                        "Nur theoretisches Lernen",
                        "Nicht notwendig",
                        "Umgang mit realen Situationen"
                },
                {
                        "Wie beeinflusst die Bildungsumgebung?",
                        "Kein Einfluss",
                        "Beeinflusst Lernergebnisse",
                        "Beeinflusst nur Lehrer",
                        "Beeinflusst nur Infrastruktur",
                        "Beeinflusst Lernergebnisse"
                },
                {
                        "Welche Rolle spielt Selbststudium?",
                        "Nicht notwendig",
                        "Hilft Wissen und Fähigkeiten zu verbessern",
                        "Nur für kluge Menschen",
                        "Erzeugt Druck",
                        "Hilft Wissen und Fähigkeiten zu verbessern"
                },
                {
                        "Was hilft Bürgererziehung zu formen?",
                        "Sinn für gesellschaftliche Verantwortung",
                        "Nur rechtliches Wissen",
                        "Berufliche Fähigkeiten",
                        "Körperliche Stärke",
                        "Sinn für gesellschaftliche Verantwortung"
                },
                {
                        "Was bedeutet 'Lernen geht Hand in Hand mit Praxis'?",
                        "Nur theoretisches Lernen",
                        "Theorie mit Praxis kombinieren",
                        "Nur Praxis",
                        "Keine Notwendigkeit zu studieren",
                        "Theorie mit Praxis kombinieren"
                },
                {
                        "Was bedeutet Bildungsgleichheit?",
                        "Jeder hat Zugang zu Bildung",
                        "Nur kluge Menschen können studieren",
                        "Nur reiche Menschen können studieren",
                        "Keine Geschlechterdiskriminierung",
                        "Jeder hat Zugang zu Bildung"
                },
                {
                        "Welche Rolle spielt Bildung für die Gesellschaft?",
                        "Nicht wichtig",
                        "Entwicklung menschlicher Ressourcen",
                        "Dient nur Individuen",
                        "Dient nur der Wirtschaft",
                        "Entwicklung menschlicher Ressourcen"
                },
                {
                        "Wobei hilft Lernen den Menschen?",
                        "Einkommen steigern",
                        "Verständnis erweitern",
                        "Persönliche Entwicklung",
                        "All das oben Genannte",
                        "All das oben Genannte"
                },
                {
                        "Worauf konzentriert sich moderne Bildung?",
                        "Auswendiglernen",
                        "Kreatives Denken",
                        "Prüfungen",
                        "Starre Disziplin",
                        "Kreatives Denken"
                },
                {
                        "Wobei hilft Technologie in der Bildung?",
                        "Verursacht Ablenkung",
                        "Verbessert die Lerneffektivität",
                        "Reduziert Qualität",
                        "Nicht notwendig",
                        "Verbessert die Lerneffektivität"
                },
                {
                        "Was ist die Verantwortung der Schüler beim Lernen?",
                        "Oberflächliches Studium",
                        "Proaktiv und bewusst",
                        "Nur Vorlesungen hören",
                        "Von anderen abhängen",
                        "Proaktiv und bewusst"
                },
                {
                        "Was ist die wichtigste Rolle der Bildung?",
                        "Wissen übertragen",
                        "Ganzheitliche Individuen formen",
                        "Prüfen",
                        "Bewerten",
                        "Ganzheitliche Individuen formen"
                }
        };
    }
    // Hàm trả về câu hỏi về giáo dục tiếng Trung
    private String[][] educationChinese(){
        return new String[][] {
                {
                        "什么是教育？",
                        "娱乐活动",
                        "传授知识和技能的过程",
                        "商业活动",
                        "休息活动",
                        "传授知识和技能的过程"
                },
                {
                        "教育的主要目标是什么？",
                        "制造压力",
                        "全面发展人才",
                        "仅仅传授知识",
                        "仅仅训练纪律",
                        "全面发展人才"
                },
                {
                        "教育帮助人类发展什么？",
                        "体质",
                        "智力",
                        "道德",
                        "以上所有因素",
                        "以上所有因素"
                },
                {
                        "教育从哪里开始？",
                        "学校",
                        "家庭",
                        "社会",
                        "家庭和学校",
                        "家庭和学校"
                },
                {
                        "终身学习有什么意义？",
                        "没有必要",
                        "帮助适应社会变化",
                        "仅适用于学生",
                        "浪费时间",
                        "帮助适应社会变化"
                },
                {
                        "教师的作用是什么？",
                        "传授知识和指导",
                        "仅仅打分",
                        "管理学生",
                        "布置作业",
                        "传授知识和指导"
                },
                {
                        "学生在学习中的作用是什么？",
                        "被动听课",
                        "主动学习",
                        "仅仅为考试而学",
                        "依赖教师",
                        "主动学习"
                },
                {
                        "道德教育的目的是什么？",
                        "控制行为",
                        "品格形成",
                        "制造压力",
                        "仅仅教授礼仪",
                        "品格形成"
                },
                {
                        "生活技能教育对学生有什么帮助？",
                        "娱乐",
                        "应对现实情况",
                        "仅仅理论学习",
                        "不必要",
                        "应对现实情况"
                },
                {
                        "教育环境如何产生影响？",
                        "没有影响",
                        "影响学习成果",
                        "仅影响教师",
                        "仅影响基础设施",
                        "影响学习成果"
                },
                {
                        "自学起什么作用？",
                        "不必要",
                        "帮助提高知识和技能",
                        "仅适用于聪明人",
                        "造成压力",
                        "帮助提高知识和技能"
                },
                {
                        "公民教育帮助形成什么？",
                        "社会责任感",
                        "仅仅法律知识",
                        "职业技能",
                        "体力",
                        "社会责任感"
                },
                {
                        "'学行并重'意味着什么？",
                        "仅仅理论学习",
                        "理论与实践相结合",
                        "仅仅实践",
                        "不需要学习",
                        "理论与实践相结合"
                },
                {
                        "教育平等意味着什么？",
                        "人人都能接受教育",
                        "仅聪明人能学习",
                        "仅富人能学习",
                        "不分性别",
                        "人人都能接受教育"
                },
                {
                        "教育对社会起什么作用？",
                        "不重要",
                        "发展人力资源",
                        "仅服务个人",
                        "仅服务经济",
                        "发展人力资源"
                },
                {
                        "学习帮助人类什么？",
                        "增加收入",
                        "扩大理解",
                        "个人发展",
                        "以上所有",
                        "以上所有"
                },
                {
                        "现代教育注重什么？",
                        "死记硬背",
                        "创造性思维",
                        "考试",
                        "严格纪律",
                        "创造性思维"
                },
                {
                        "教育中的技术有什么帮助？",
                        "造成分心",
                        "提高学习效率",
                        "降低质量",
                        "不必要",
                        "提高学习效率"
                },
                {
                        "学生在学习中的责任是什么？",
                        "敷衍学习",
                        "主动和自觉",
                        "仅仅听讲",
                        "依赖他人",
                        "主动和自觉"
                },
                {
                        "教育最重要的作用是什么？",
                        "传授知识",
                        "培养全面发展的人",
                        "考试",
                        "排名",
                        "培养全面发展的人"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Hàn
    private String[][] educationKorean(){
        return new String[][] {
                {
                        "교육이란 무엇인가요？",
                        "오락 활동",
                        "지식과 기술을 전달하는 과정",
                        "상업 활동",
                        "휴식 활동",
                        "지식과 기술을 전달하는 과정"
                },
                {
                        "교육의 주요 목표는 무엇인가요？",
                        "압박감 조성",
                        "포괄적인 인간 발달",
                        "지식 전달만",
                        "규율 훈련만",
                        "포괄적인 인간 발달"
                },
                {
                        "교육은 인간이 무엇을 개발하는 데 도움이 됩니까？",
                        "신체적 측면",
                        "지능",
                        "도덕성",
                        "위의 모든 요소",
                        "위의 모든 요소"
                },
                {
                        "교육은 어디서 시작됩니까？",
                        "학교",
                        "가정",
                        "사회",
                        "가정과 학교",
                        "가정과 학교"
                },
                {
                        "평생학습의 의미는 무엇인가요？",
                        "필요하지 않음",
                        "사회 변화에 적응하는 데 도움",
                        "학생들만을 위한 것",
                        "시간 낭비",
                        "사회 변화에 적응하는 데 도움"
                },
                {
                        "교사의 역할은 무엇인가요？",
                        "지식 전달과 지도",
                        "점수만 매기기",
                        "학생 관리",
                        "숙제 내주기",
                        "지식 전달과 지도"
                },
                {
                        "학습에서 학생의 역할은 무엇인가요？",
                        "수동적 듣기",
                        "능동적 학습",
                        "시험만을 위한 공부",
                        "교사에 의존",
                        "능동적 학습"
                },
                {
                        "도덕 교육의 목적은 무엇인가요？",
                        "행동 통제",
                        "인격 형성",
                        "압박감 조성",
                        "예의범절만 가르치기",
                        "인격 형성"
                },
                {
                        "생활 기술 교육은 학생들에게 어떤 도움을 줍니까？",
                        "오락",
                        "현실 상황 대처",
                        "이론 학습만",
                        "필요하지 않음",
                        "현실 상황 대처"
                },
                {
                        "교육 환경은 어떤 영향을 미칩니까？",
                        "영향 없음",
                        "학습 결과에 영향",
                        "교사에게만 영향",
                        "인프라에만 영향",
                        "학습 결과에 영향"
                },
                {
                        "자기주도학습은 어떤 역할을 합니까？",
                        "필요하지 않음",
                        "지식과 기술 향상에 도움",
                        "똑똑한 사람들만을 위한 것",
                        "압박감 조성",
                        "지식과 기술 향상에 도움"
                },
                {
                        "시민 교육은 무엇을 형성하는 데 도움이 됩니까？",
                        "사회적 책임감",
                        "법률 지식만",
                        "직업 기술",
                        "체력",
                        "사회적 책임감"
                },
                {
                        "'학습과 실천이 함께 간다'는 것은 무엇을 의미합니까？",
                        "이론 학습만",
                        "이론과 실천의 결합",
                        "실천만",
                        "공부할 필요 없음",
                        "이론과 실천의 결합"
                },
                {
                        "교육 평등은 무엇을 의미합니까？",
                        "모든 사람이 교육에 접근할 수 있음",
                        "똑똑한 사람만 공부할 수 있음",
                        "부유한 사람만 공부할 수 있음",
                        "성별 차별 없음",
                        "모든 사람이 교육에 접근할 수 있음"
                },
                {
                        "교육은 사회에 어떤 역할을 합니까？",
                        "중요하지 않음",
                        "인적 자원 개발",
                        "개인만을 위한 것",
                        "경제만을 위한 것",
                        "인적 자원 개발"
                },
                {
                        "학습은 인간에게 무엇을 도와줍니까？",
                        "수입 증가",
                        "이해 확장",
                        "개인 발전",
                        "위의 모든 것",
                        "위의 모든 것"
                },
                {
                        "현대 교육은 무엇에 중점을 둡니까？",
                        "암기",
                        "창의적 사고",
                        "시험",
                        "엄격한 규율",
                        "창의적 사고"
                },
                {
                        "교육에서 기술은 무엇을 도와줍니까？",
                        "주의 산만 야기",
                        "학습 효과 향상",
                        "품질 저하",
                        "필요하지 않음",
                        "학습 효과 향상"
                },
                {
                        "학습에서 학생의 책임은 무엇입니까？",
                        "피상적 공부",
                        "적극적이고 의식적",
                        "강의만 듣기",
                        "다른 사람에게 의존",
                        "적극적이고 의식적"
                },
                {
                        "교육의 가장 중요한 역할은 무엇입니까？",
                        "지식 전달",
                        "전인적 개인 형성",
                        "시험",
                        "순위 매기기",
                        "전인적 개인 형성"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Nga
    private String[][] educationRussian(){
        return new String[][] {
                {
                        "Что такое образование?",
                        "Развлекательная деятельность",
                        "Процесс передачи знаний и навыков",
                        "Деловая деятельность",
                        "Деятельность отдыха",
                        "Процесс передачи знаний и навыков"
                },
                {
                        "Какова основная цель образования?",
                        "Создание давления",
                        "Всестороннее развитие человека",
                        "Только передача знаний",
                        "Только обучение дисциплине",
                        "Всестороннее развитие человека"
                },
                {
                        "Что образование помогает людям развивать?",
                        "Физические аспекты",
                        "Интеллект",
                        "Мораль",
                        "Все вышеперечисленные факторы",
                        "Все вышеперечисленные факторы"
                },
                {
                        "Где начинается образование?",
                        "Школа",
                        "Семья",
                        "Общество",
                        "Семья и школа",
                        "Семья и школа"
                },
                {
                        "Каково значение обучения на протяжении всей жизни?",
                        "Не нужно",
                        "Помогает адаптироваться к социальным изменениям",
                        "Только для студентов",
                        "Трата времени",
                        "Помогает адаптироваться к социальным изменениям"
                },
                {
                        "Какова роль учителей?",
                        "Передача знаний и руководство",
                        "Только выставление оценок",
                        "Управление учениками",
                        "Задание домашних заданий",
                        "Передача знаний и руководство"
                },
                {
                        "Какова роль учеников в обучении?",
                        "Пассивное слушание",
                        "Активное обучение",
                        "Учеба только для экзаменов",
                        "Зависимость от учителей",
                        "Активное обучение"
                },
                {
                        "Какова цель нравственного образования?",
                        "Контроль поведения",
                        "Формирование характера",
                        "Создание давления",
                        "Обучение только этикету",
                        "Формирование характера"
                },
                {
                        "Чем образование жизненным навыкам помогает ученикам?",
                        "Развлечение",
                        "Справляться с реальными ситуациями",
                        "Только теоретическое обучение",
                        "Не нужно",
                        "Справляться с реальными ситуациями"
                },
                {
                        "Как влияет образовательная среда?",
                        "Не влияет",
                        "Влияет на результаты обучения",
                        "Влияет только на учителей",
                        "Влияет только на инфраструктуру",
                        "Влияет на результаты обучения"
                },
                {
                        "Какую роль играет самообразование?",
                        "Не нужно",
                        "Помогает улучшить знания и навыки",
                        "Только для умных людей",
                        "Создает давление",
                        "Помогает улучшить знания и навыки"
                },
                {
                        "Что помогает формировать гражданское образование?",
                        "Чувство социальной ответственности",
                        "Только правовые знания",
                        "Профессиональные навыки",
                        "Физическая сила",
                        "Чувство социальной ответственности"
                },
                {
                        "Что означает 'обучение идет рука об руку с практикой'?",
                        "Только теоретическое обучение",
                        "Сочетание теории с практикой",
                        "Только практика",
                        "Нет нужды учиться",
                        "Сочетание теории с практикой"
                },
                {
                        "Что означает равенство в образовании?",
                        "У всех есть доступ к образованию",
                        "Только умные люди могут учиться",
                        "Только богатые люди могут учиться",
                        "Нет дискриминации по полу",
                        "У всех есть доступ к образованию"
                },
                {
                        "Какую роль образование играет для общества?",
                        "Не важно",
                        "Развитие человеческих ресурсов",
                        "Служит только индивидам",
                        "Служит только экономике",
                        "Развитие человеческих ресурсов"
                },
                {
                        "Чем обучение помогает людям?",
                        "Увеличить доход",
                        "Расширить понимание",
                        "Личностное развитие",
                        "Все вышеперечисленное",
                        "Все вышеперечисленное"
                },
                {
                        "На чем сосредоточено современное образование?",
                        "Заучивание наизусть",
                        "Творческое мышление",
                        "Экзамены",
                        "Жесткая дисциплина",
                        "Творческое мышление"
                },
                {
                        "Чем помогает технология в образовании?",
                        "Вызывает отвлечение",
                        "Повышает эффективность обучения",
                        "Снижает качество",
                        "Не нужно",
                        "Повышает эффективность обучения"
                },
                {
                        "Какова ответственность учеников в обучении?",
                        "Поверхностное изучение",
                        "Проактивность и осознанность",
                        "Только слушать лекции",
                        "Зависеть от других",
                        "Проактивность и осознанность"
                },
                {
                        "Какова самая важная роль образования?",
                        "Передача знаний",
                        "Формирование всесторонне развитых личностей",
                        "Экзамены",
                        "Ранжирование",
                        "Формирование всесторонне развитых личностей"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Hindi
    private String[][] educationHindi(){
        return new String[][] {
                {
                        "शिक्षा क्या है?",
                        "मनोरंजन गतिविधि",
                        "ज्ञान और कौशल के संचारण की प्रक्रिया",
                        "व्यावसायिक गतिविधि",
                        "आराम की गतिविधि",
                        "ज्ञान और कौशल के संचारण की प्रक्रिया"
                },
                {
                        "शिक्षा का मुख्य लक्ष्य क्या है?",
                        "दबाव बनाना",
                        "व्यापक मानव विकास",
                        "केवल ज्ञान का संचार",
                        "केवल अनुशासन की ट्रेनिंग",
                        "व्यापक मानव विकास"
                },
                {
                        "शिक्षा मनुष्यों को क्या विकसित करने में मदद करती है?",
                        "शारीरिक पहलू",
                        "बुद्धि",
                        "नैतिकता",
                        "उपरोक्त सभी कारक",
                        "उपरोक्त सभी कारक"
                },
                {
                        "शिक्षा कहाँ से शुरू होती है?",
                        "स्कूल",
                        "परिवार",
                        "समाज",
                        "परिवार और स्कूल",
                        "परिवार और स्कूल"
                },
                {
                        "आजीवन अधिगम का क्या अर्थ है?",
                        "आवश्यक नहीं",
                        "सामाजिक परिवर्तनों के अनुकूल होने में मदद करता है",
                        "केवल छात्रों के लिए",
                        "समय की बर्बादी",
                        "सामाजिक परिवर्तनों के अनुकूल होने में मदद करता है"
                },
                {
                        "शिक्षकों की भूमिका क्या है?",
                        "ज्ञान का संचार और मार्गदर्शन",
                        "केवल अंक देना",
                        "छात्रों का प्रबंधन",
                        "गृहकार्य देना",
                        "ज्ञान का संचार और मार्गदर्शन"
                },
                {
                        "अधिगम में छात्रों की भूमिका क्या है?",
                        "निष्क्रिय सुनना",
                        "सक्रिय अधिगम",
                        "केवल परीक्षाओं के लिए पढ़ना",
                        "शिक्षकों पर निर्भर रहना",
                        "सक्रिय अधिगम"
                },
                {
                        "नैतिक शिक्षा का उद्देश्य क्या है?",
                        "व्यवहार को नियंत्रित करना",
                        "चरित्र निर्माण",
                        "दबाव बनाना",
                        "केवल शिष्टाचार सिखाना",
                        "चरित्र निर्माण"
                },
                {
                        "जीवन कौशल शिक्षा छात्रों की कैसे मदद करती है?",
                        "मनोरंजन",
                        "वास्तविक परिस्थितियों से निपटना",
                        "केवल सैद्धांतिक अधिगम",
                        "आवश्यक नहीं",
                        "वास्तविक परिस्थितियों से निपटना"
                },
                {
                        "शैक्षिक वातावरण कैसे प्रभावित करता है?",
                        "कोई प्रभाव नहीं",
                        "अधिगम परिणामों को प्रभावित करता है",
                        "केवल शिक्षकों को प्रभावित करता है",
                        "केवल बुनियादी ढांचे को प्रभावित करता है",
                        "अधिगम परिणामों को प्रभावित करता है"
                },
                {
                        "स्वाध्याय की क्या भूमिका है?",
                        "आवश्यक नहीं",
                        "ज्ञान और कौशल सुधारने में मदद करता है",
                        "केवल बुद्धिमान लोगों के लिए",
                        "दबाव बनाता है",
                        "ज्ञान और कौशल सुधारने में मदद करता है"
                },
                {
                        "नागरिक शिक्षा क्या गठन करने में मदद करती है?",
                        "सामाजिक जिम्मेदारी की भावना",
                        "केवल कानूनी ज्ञान",
                        "व्यावसायिक कौशल",
                        "शारीरिक शक्ति",
                        "सामाजिक जिम्मेदारी की भावना"
                },
                {
                        "'सीखना अभ्यास के साथ चलता है' का क्या अर्थ है?",
                        "केवल सैद्धांतिक अधिगम",
                        "सिद्धांत को अभ्यास के साथ जोड़ना",
                        "केवल अभ्यास",
                        "पढ़ने की जरूरत नहीं",
                        "सिद्धांत को अभ्यास के साथ जोड़ना"
                },
                {
                        "शैक्षिक समानता का क्या अर्थ है?",
                        "सभी के पास शिक्षा तक पहुंच है",
                        "केवल बुद्धिमान लोग अध्ययन कर सकते हैं",
                        "केवल अमीर लोग अध्ययन कर सकते हैं",
                        "लिंग भेदभाव नहीं",
                        "सभी के पास शिक्षा तक पहुंच है"
                },
                {
                        "समाज के लिए शिक्षा की क्या भूमिका है?",
                        "महत्वपूर्ण नहीं",
                        "मानव संसाधन विकास",
                        "केवल व्यक्तियों की सेवा",
                        "केवल अर्थव्यवस्था की सेवा",
                        "मानव संसाधन विकास"
                },
                {
                        "अधिगम मनुष्यों की कैसे मदद करता है?",
                        "आय बढ़ाना",
                        "समझ का विस्तार",
                        "व्यक्तिगत विकास",
                        "उपरोक्त सभी",
                        "उपरोक्त सभी"
                },
                {
                        "आधुनिक शिक्षा किस पर ध्यान देती है?",
                        "रटकर सीखना",
                        "रचनात्मक सोच",
                        "परीक्षाएं",
                        "कठोर अनुशासन",
                        "रचनात्मक सोच"
                },
                {
                        "शिक्षा में प्रौद्योगिकी कैसे मदद करती है?",
                        "विकर्षण का कारण",
                        "अधिगम प्रभावशीलता में सुधार",
                        "गुणवत्ता कम करती है",
                        "आवश्यक नहीं",
                        "अधिगम प्रभावशीलता में सुधार"
                },
                {
                        "अधिगम में छात्रों की जिम्मेदारी क्या है?",
                        "सतही अध्ययन",
                        "सक्रिय और सचेत",
                        "केवल व्याख्यान सुनना",
                        "दूसरों पर निर्भर रहना",
                        "सक्रिय और सचेत"
                },
                {
                        "शिक्षा की सबसे महत्वपूर्ण भूमिका क्या है?",
                        "ज्ञान का संचार",
                        "सर्वांगीण व्यक्तित्व का निर्माण",
                        "परीक्षा",
                        "रैंकिंग",
                        "सर्वांगीण व्यक्तित्व का निर्माण"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Nhật
    private String[][] educationJapanese(){
        return new String[][] {
                {
                        "教育とは何ですか？",
                        "娯楽活動",
                        "知識と技能を伝達する過程",
                        "商業活動",
                        "休息活動",
                        "知識と技能を伝達する過程"
                },
                {
                        "教育の主な目標は何ですか？",
                        "プレッシャーを作ること",
                        "包括的な人間開発",
                        "知識の伝達のみ",
                        "規律の訓練のみ",
                        "包括的な人間開発"
                },
                {
                        "教育は人間の何を発達させるのに役立ちますか？",
                        "身体的側面",
                        "知能",
                        "道徳",
                        "上記のすべての要素",
                        "上記のすべての要素"
                },
                {
                        "教育はどこから始まりますか？",
                        "学校",
                        "家庭",
                        "社会",
                        "家庭と学校",
                        "家庭と学校"
                },
                {
                        "生涯学習の意味は何ですか？",
                        "必要ない",
                        "社会の変化に適応するのに役立つ",
                        "学生のみのため",
                        "時間の無駄",
                        "社会の変化に適応するのに役立つ"
                },
                {
                        "教師の役割は何ですか？",
                        "知識の伝達と指導",
                        "採点のみ",
                        "学生の管理",
                        "宿題を出すこと",
                        "知識の伝達と指導"
                },
                {
                        "学習における学生の役割は何ですか？",
                        "受動的な聞き取り",
                        "能動的学習",
                        "試験のためだけの勉強",
                        "教師に依存",
                        "能動的学習"
                },
                {
                        "道徳教育の目的は何ですか？",
                        "行動の制御",
                        "人格形成",
                        "プレッシャーを作ること",
                        "エチケットのみを教えること",
                        "人格形成"
                },
                {
                        "ライフスキル教育は学生にどのような助けとなりますか？",
                        "娯楽",
                        "現実的な状況への対処",
                        "理論的学習のみ",
                        "必要ない",
                        "現実的な状況への対処"
                },
                {
                        "教育環境はどのように影響しますか？",
                        "影響しない",
                        "学習成果に影響する",
                        "教師にのみ影響する",
                        "インフラにのみ影響する",
                        "学習成果に影響する"
                },
                {
                        "自主学習はどのような役割を果たしますか？",
                        "必要ない",
                        "知識と技能の向上に役立つ",
                        "賢い人のみのため",
                        "プレッシャーを作る",
                        "知識と技能の向上に役立つ"
                },
                {
                        "市民教育は何の形成に役立ちますか？",
                        "社会的責任感",
                        "法的知識のみ",
                        "職業技能",
                        "体力",
                        "社会的責任感"
                },
                {
                        "「学習は実践と手を携える」とはどういう意味ですか？",
                        "理論的学習のみ",
                        "理論と実践の結合",
                        "実践のみ",
                        "勉強する必要がない",
                        "理論と実践の結合"
                },
                {
                        "教育平等とはどういう意味ですか？",
                        "誰もが教育にアクセスできる",
                        "賢い人のみが勉強できる",
                        "裕福な人のみが勉強できる",
                        "性別差別がない",
                        "誰もが教育にアクセスできる"
                },
                {
                        "教育は社会にとってどのような役割を果たしますか？",
                        "重要ではない",
                        "人材開発",
                        "個人にのみ奉仕",
                        "経済にのみ奉仕",
                        "人材開発"
                },
                {
                        "学習は人間にとって何の助けとなりますか？",
                        "収入の増加",
                        "理解の拡大",
                        "個人的発展",
                        "上記のすべて",
                        "上記のすべて"
                },
                {
                        "現代教育は何に焦点を当てていますか？",
                        "暗記",
                        "創造的思考",
                        "試験",
                        "厳格な規律",
                        "創造的思考"
                },
                {
                        "教育における技術は何に役立ちますか？",
                        "気を散らすことを引き起こす",
                        "学習効果を向上させる",
                        "質を下げる",
                        "必要ない",
                        "学習効果を向上させる"
                },
                {
                        "学習における学生の責任は何ですか？",
                        "表面的な勉強",
                        "積極的で意識的",
                        "講義を聞くだけ",
                        "他人に依存",
                        "積極的で意識的"
                },
                {
                        "教育の最も重要な役割は何ですか？",
                        "知識の伝達",
                        "全人的個人の形成",
                        "試験",
                        "ランキング",
                        "全人的個人の形成"
                }
        };
    }
    // Hàm trả về câu hỏi về giáo dục tiếng Bồ Đào Nha
    private String[][] educationPortuguese(){
        return new String[][] {
                {
                        "O que é educação?",
                        "Atividade de entretenimento",
                        "Processo de transmissão de conhecimento e habilidades",
                        "Atividade comercial",
                        "Atividade de descanso",
                        "Processo de transmissão de conhecimento e habilidades"
                },
                {
                        "Qual é o principal objetivo da educação?",
                        "Criar pressão",
                        "Desenvolvimento humano abrangente",
                        "Apenas transmitir conhecimento",
                        "Apenas treinar disciplina",
                        "Desenvolvimento humano abrangente"
                },
                {
                        "A educação ajuda os humanos a desenvolver o quê?",
                        "Aspectos físicos",
                        "Inteligência",
                        "Moralidade",
                        "Todos os fatores acima",
                        "Todos os fatores acima"
                },
                {
                        "Onde a educação começa?",
                        "Escola",
                        "Família",
                        "Sociedade",
                        "Família e escola",
                        "Família e escola"
                },
                {
                        "Qual é o significado da aprendizagem ao longo da vida?",
                        "Não necessário",
                        "Ajuda a adaptar-se às mudanças sociais",
                        "Apenas para estudantes",
                        "Perda de tempo",
                        "Ajuda a adaptar-se às mudanças sociais"
                },
                {
                        "Qual é o papel dos professores?",
                        "Transmitir conhecimento e orientação",
                        "Apenas dar notas",
                        "Gerenciar estudantes",
                        "Dar tarefas de casa",
                        "Transmitir conhecimento e orientação"
                },
                {
                        "Qual é o papel dos estudantes na aprendizagem?",
                        "Escuta passiva",
                        "Aprendizagem ativa",
                        "Estudar apenas para exames",
                        "Depender de professores",
                        "Aprendizagem ativa"
                },
                {
                        "Qual é o propósito da educação moral?",
                        "Controlar comportamento",
                        "Formação de caráter",
                        "Criar pressão",
                        "Ensinar apenas etiqueta",
                        "Formação de caráter"
                },
                {
                        "A educação de habilidades para a vida ajuda os estudantes com o quê?",
                        "Entretenimento",
                        "Lidar com situações da vida real",
                        "Apenas aprendizagem teórica",
                        "Não necessário",
                        "Lidar com situações da vida real"
                },
                {
                        "Como o ambiente educacional influencia?",
                        "Nenhuma influência",
                        "Afeta os resultados da aprendizagem",
                        "Afeta apenas professores",
                        "Afeta apenas infraestrutura",
                        "Afeta os resultados da aprendizagem"
                },
                {
                        "Que papel o autoestudo desempenha?",
                        "Não necessário",
                        "Ajuda a melhorar conhecimento e habilidades",
                        "Apenas para pessoas inteligentes",
                        "Cria pressão",
                        "Ajuda a melhorar conhecimento e habilidades"
                },
                {
                        "A educação cívica ajuda a formar o quê?",
                        "Senso de responsabilidade social",
                        "Apenas conhecimento legal",
                        "Habilidades profissionais",
                        "Força física",
                        "Senso de responsabilidade social"
                },
                {
                        "O que significa 'aprender vai de mãos dadas com a prática'?",
                        "Apenas aprendizagem teórica",
                        "Combinar teoria com prática",
                        "Apenas prática",
                        "Não precisa estudar",
                        "Combinar teoria com prática"
                },
                {
                        "O que significa igualdade educacional?",
                        "Todos têm acesso à educação",
                        "Apenas pessoas inteligentes podem estudar",
                        "Apenas pessoas ricas podem estudar",
                        "Sem discriminação de gênero",
                        "Todos têm acesso à educação"
                },
                {
                        "Que papel a educação desempenha para a sociedade?",
                        "Não importante",
                        "Desenvolvimento de recursos humanos",
                        "Serve apenas indivíduos",
                        "Serve apenas economia",
                        "Desenvolvimento de recursos humanos"
                },
                {
                        "A aprendizagem ajuda os humanos com o quê?",
                        "Aumentar renda",
                        "Expandir compreensão",
                        "Desenvolvimento pessoal",
                        "Todas as opções acima",
                        "Todas as opções acima"
                },
                {
                        "Em que a educação moderna se concentra?",
                        "Memorização",
                        "Pensamento criativo",
                        "Testes",
                        "Disciplina rígida",
                        "Pensamento criativo"
                },
                {
                        "A tecnologia na educação ajuda com o quê?",
                        "Causa distração",
                        "Melhora a eficácia da aprendizagem",
                        "Reduz qualidade",
                        "Não necessário",
                        "Melhora a eficácia da aprendizagem"
                },
                {
                        "Qual é a responsabilidade dos estudantes na aprendizagem?",
                        "Estudo superficial",
                        "Proativo e consciente",
                        "Apenas ouvir palestras",
                        "Depender de outros",
                        "Proativo e consciente"
                },
                {
                        "Qual é o papel mais importante da educação?",
                        "Transmitir conhecimento",
                        "Formar indivíduos bem-arredondados",
                        "Testes",
                        "Classificação",
                        "Formar indivíduos bem-arredondados"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Hà Lan
    private String[][] educationDutch(){
        return new String[][] {
                {
                        "Wat is onderwijs?",
                        "Entertainmentactiviteit",
                        "Proces van kennisoverdracht en vaardigheden",
                        "Zakelijke activiteit",
                        "Rustactiviteit",
                        "Proces van kennisoverdracht en vaardigheden"
                },
                {
                        "Wat is het hoofddoel van onderwijs?",
                        "Druk creëren",
                        "Uitgebreide menselijke ontwikkeling",
                        "Alleen kennis overdragen",
                        "Alleen discipline trainen",
                        "Uitgebreide menselijke ontwikkeling"
                },
                {
                        "Wat helpt onderwijs mensen te ontwikkelen?",
                        "Fysieke aspecten",
                        "Intelligentie",
                        "Moraliteit",
                        "Alle bovenstaande factoren",
                        "Alle bovenstaande factoren"
                },
                {
                        "Waar begint onderwijs?",
                        "School",
                        "Familie",
                        "Samenleving",
                        "Familie en school",
                        "Familie en school"
                },
                {
                        "Wat is de betekenis van levenslang leren?",
                        "Niet nodig",
                        "Helpt aanpassen aan sociale veranderingen",
                        "Alleen voor studenten",
                        "Tijdverspilling",
                        "Helpt aanpassen aan sociale veranderingen"
                },
                {
                        "Wat is de rol van leraren?",
                        "Kennis overdragen en begeleiden",
                        "Alleen cijfers geven",
                        "Studenten beheren",
                        "Huiswerk geven",
                        "Kennis overdragen en begeleiden"
                },
                {
                        "Wat is de rol van studenten bij het leren?",
                        "Passief luisteren",
                        "Actief leren",
                        "Alleen studeren voor examens",
                        "Afhankelijk van leraren",
                        "Actief leren"
                },
                {
                        "Wat is het doel van moreel onderwijs?",
                        "Gedrag controleren",
                        "Karaktervorming",
                        "Druk creëren",
                        "Alleen etiquette onderwijzen",
                        "Karaktervorming"
                },
                {
                        "Waarmee helpt levensvaardigheden onderwijs studenten?",
                        "Entertainment",
                        "Omgaan met echte situaties",
                        "Alleen theoretisch leren",
                        "Niet nodig",
                        "Omgaan met echte situaties"
                },
                {
                        "Hoe beïnvloedt de onderwijsomgeving?",
                        "Geen invloed",
                        "Beïnvloedt leerresultaten",
                        "Beïnvloedt alleen leraren",
                        "Beïnvloedt alleen infrastructuur",
                        "Beïnvloedt leerresultaten"
                },
                {
                        "Welke rol speelt zelfstudie?",
                        "Niet nodig",
                        "Helpt kennis en vaardigheden verbeteren",
                        "Alleen voor slimme mensen",
                        "Creëert druk",
                        "Helpt kennis en vaardigheden verbeteren"
                },
                {
                        "Wat helpt burgerschapsonderwijs vormen?",
                        "Gevoel van sociale verantwoordelijkheid",
                        "Alleen juridische kennis",
                        "Professionele vaardigheden",
                        "Fysieke kracht",
                        "Gevoel van sociale verantwoordelijkheid"
                },
                {
                        "Wat betekent 'leren gaat hand in hand met praktijk'?",
                        "Alleen theoretisch leren",
                        "Theorie combineren met praktijk",
                        "Alleen praktijk",
                        "Hoeft niet te studeren",
                        "Theorie combineren met praktijk"
                },
                {
                        "Wat betekent onderwijsgelijkheid?",
                        "Iedereen heeft toegang tot onderwijs",
                        "Alleen slimme mensen kunnen studeren",
                        "Alleen rijke mensen kunnen studeren",
                        "Geen geslachtsdiscriminatie",
                        "Iedereen heeft toegang tot onderwijs"
                },
                {
                        "Welke rol speelt onderwijs voor de samenleving?",
                        "Niet belangrijk",
                        "Ontwikkeling van menselijke hulpbronnen",
                        "Dient alleen individuen",
                        "Dient alleen economie",
                        "Ontwikkeling van menselijke hulpbronnen"
                },
                {
                        "Waarmee helpt leren mensen?",
                        "Inkomen verhogen",
                        "Begrip uitbreiden",
                        "Persoonlijke ontwikkeling",
                        "Alle bovenstaande",
                        "Alle bovenstaande"
                },
                {
                        "Waar richt modern onderwijs zich op?",
                        "Memoriseren",
                        "Creatief denken",
                        "Testen",
                        "Rigide discipline",
                        "Creatief denken"
                },
                {
                        "Waarmee helpt technologie in het onderwijs?",
                        "Veroorzaakt afleiding",
                        "Verbetert leereffectiviteit",
                        "Vermindert kwaliteit",
                        "Niet nodig",
                        "Verbetert leereffectiviteit"
                },
                {
                        "Wat is de verantwoordelijkheid van studenten bij het leren?",
                        "Oppervlakkige studie",
                        "Proactief en bewust",
                        "Alleen naar lezingen luisteren",
                        "Afhankelijk van anderen",
                        "Proactief en bewust"
                },
                {
                        "Wat is de belangrijkste rol van onderwijs?",
                        "Kennis overdragen",
                        "Allround individuen vormen",
                        "Testen",
                        "Rangschikken",
                        "Allround individuen vormen"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Ả Rập
    private String[][] educationArabic(){
        return new String[][] {
                {
                        "ما هو التعليم؟",
                        "نشاط ترفيهي",
                        "عملية نقل المعرفة والمهارات",
                        "نشاط تجاري",
                        "نشاط راحة",
                        "عملية نقل المعرفة والمهارات"
                },
                {
                        "ما هو الهدف الرئيسي للتعليم؟",
                        "خلق الضغط",
                        "التنمية البشرية الشاملة",
                        "نقل المعرفة فقط",
                        "تدريب الانضباط فقط",
                        "التنمية البشرية الشاملة"
                },
                {
                        "ما الذي يساعد التعليم البشر على تطويره؟",
                        "الجوانب الجسدية",
                        "الذكاء",
                        "الأخلاق",
                        "جميع العوامل المذكورة أعلاه",
                        "جميع العوامل المذكورة أعلاه"
                },
                {
                        "أين يبدأ التعليم؟",
                        "المدرسة",
                        "العائلة",
                        "المجتمع",
                        "العائلة والمدرسة",
                        "العائلة والمدرسة"
                },
                {
                        "ما هو معنى التعلم مدى الحياة؟",
                        "غير ضروري",
                        "يساعد على التكيف مع التغيرات الاجتماعية",
                        "للطلاب فقط",
                        "مضيعة للوقت",
                        "يساعد على التكيف مع التغيرات الاجتماعية"
                },
                {
                        "ما هو دور المعلمين؟",
                        "نقل المعرفة والتوجيه",
                        "إعطاء الدرجات فقط",
                        "إدارة الطلاب",
                        "إعطاء الواجبات المنزلية",
                        "نقل المعرفة والتوجيه"
                },
                {
                        "ما هو دور الطلاب في التعلم؟",
                        "الاستماع السلبي",
                        "التعلم النشط",
                        "الدراسة للامتحانات فقط",
                        "الاعتماد على المعلمين",
                        "التعلم النشط"
                },
                {
                        "ما هو الغرض من التربية الأخلاقية؟",
                        "السيطرة على السلوك",
                        "تكوين الشخصية",
                        "خلق الضغط",
                        "تعليم الآداب فقط",
                        "تكوين الشخصية"
                },
                {
                        "بماذا يساعد تعليم المهارات الحياتية الطلاب؟",
                        "الترفيه",
                        "التعامل مع المواقف الحقيقية",
                        "التعلم النظري فقط",
                        "غير ضروري",
                        "التعامل مع المواقف الحقيقية"
                },
                {
                        "كيف تؤثر البيئة التعليمية؟",
                        "لا تأثير",
                        "تؤثر على نتائج التعلم",
                        "تؤثر على المعلمين فقط",
                        "تؤثر على البنية التحتية فقط",
                        "تؤثر على نتائج التعلم"
                },
                {
                        "ما الدور الذي يلعبه التعلم الذاتي؟",
                        "غير ضروري",
                        "يساعد على تحسين المعرفة والمهارات",
                        "للأذكياء فقط",
                        "يخلق ضغطاً",
                        "يساعد على تحسين المعرفة والمهارات"
                },
                {
                        "ما الذي يساعد التعليم المدني على تكوينه؟",
                        "الشعور بالمسؤولية الاجتماعية",
                        "المعرفة القانونية فقط",
                        "المهارات المهنية",
                        "القوة الجسدية",
                        "الشعور بالمسؤولية الاجتماعية"
                },
                {
                        "ماذا يعني 'التعلم يسير جنباً إلى جنب مع الممارسة'؟",
                        "التعلم النظري فقط",
                        "دمج النظرية مع الممارسة",
                        "الممارسة فقط",
                        "لا حاجة للدراسة",
                        "دمج النظرية مع الممارسة"
                },
                {
                        "ماذا تعني المساواة التعليمية؟",
                        "الجميع لديهم إمكانية الوصول إلى التعليم",
                        "الأذكياء فقط يستطيعون الدراسة",
                        "الأغنياء فقط يستطيعون الدراسة",
                        "عدم التمييز بين الجنسين",
                        "الجميع لديهم إمكانية الوصول إلى التعليم"
                },
                {
                        "ما الدور الذي يلعبه التعليم للمجتمع؟",
                        "غير مهم",
                        "تنمية الموارد البشرية",
                        "يخدم الأفراد فقط",
                        "يخدم الاقتصاد فقط",
                        "تنمية الموارد البشرية"
                },
                {
                        "بماذا يساعد التعلم البشر؟",
                        "زيادة الدخل",
                        "توسيع الفهم",
                        "التطوير الشخصي",
                        "جميع ما سبق",
                        "جميع ما سبق"
                },
                {
                        "على ماذا يركز التعليم الحديث؟",
                        "الحفظ",
                        "التفكير الإبداعي",
                        "الامتحانات",
                        "الانضباط الصارم",
                        "التفكير الإبداعي"
                },
                {
                        "بماذا تساعد التكنولوجيا في التعليم؟",
                        "تسبب الإلهاء",
                        "تحسن فعالية التعلم",
                        "تقلل الجودة",
                        "غير ضرورية",
                        "تحسن فعالية التعلم"
                },
                {
                        "ما هي مسؤولية الطلاب في التعلم؟",
                        "الدراسة السطحية",
                        "النشاط والوعي",
                        "الاستماع للمحاضرات فقط",
                        "الاعتماد على الآخرين",
                        "النشاط والوعي"
                },
                {
                        "ما هو أهم دور للتعليم؟",
                        "نقل المعرفة",
                        "تكوين أفراد متكاملين",
                        "الامتحانات",
                        "الترتيب",
                        "تكوين أفراد متكاملين"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Ý
    private String[][] educationItalian(){
        return new String[][] {
                {
                        "Che cos'è l'educazione?",
                        "Attività di intrattenimento",
                        "Processo di trasmissione di conoscenza e competenze",
                        "Attività commerciale",
                        "Attività di riposo",
                        "Processo di trasmissione di conoscenza e competenze"
                },
                {
                        "Qual è l'obiettivo principale dell'educazione?",
                        "Creare pressione",
                        "Sviluppo umano completo",
                        "Solo trasmettere conoscenza",
                        "Solo allenare la disciplina",
                        "Sviluppo umano completo"
                },
                {
                        "Cosa aiuta l'educazione a sviluppare negli esseri umani?",
                        "Aspetti fisici",
                        "Intelligenza",
                        "Moralità",
                        "Tutti i fattori sopra elencati",
                        "Tutti i fattori sopra elencati"
                },
                {
                        "Dove inizia l'educazione?",
                        "Scuola",
                        "Famiglia",
                        "Società",
                        "Famiglia e scuola",
                        "Famiglia e scuola"
                },
                {
                        "Qual è il significato dell'apprendimento permanente?",
                        "Non necessario",
                        "Aiuta ad adattarsi ai cambiamenti sociali",
                        "Solo per studenti",
                        "Spreco di tempo",
                        "Aiuta ad adattarsi ai cambiamenti sociali"
                },
                {
                        "Qual è il ruolo degli insegnanti?",
                        "Trasmettere conoscenza e orientamento",
                        "Solo dare voti",
                        "Gestire studenti",
                        "Dare compiti",
                        "Trasmettere conoscenza e orientamento"
                },
                {
                        "Qual è il ruolo degli studenti nell'apprendimento?",
                        "Ascolto passivo",
                        "Apprendimento attivo",
                        "Studiare solo per gli esami",
                        "Dipendere dagli insegnanti",
                        "Apprendimento attivo"
                },
                {
                        "Qual è lo scopo dell'educazione morale?",
                        "Controllare il comportamento",
                        "Formazione del carattere",
                        "Creare pressione",
                        "Insegnare solo l'etichetta",
                        "Formazione del carattere"
                },
                {
                        "In cosa l'educazione alle competenze per la vita aiuta gli studenti?",
                        "Intrattenimento",
                        "Affrontare situazioni reali",
                        "Solo apprendimento teorico",
                        "Non necessario",
                        "Affrontare situazioni reali"
                },
                {
                        "Come influisce l'ambiente educativo?",
                        "Nessuna influenza",
                        "Influisce sui risultati dell'apprendimento",
                        "Influisce solo sugli insegnanti",
                        "Influisce solo sull'infrastruttura",
                        "Influisce sui risultati dell'apprendimento"
                },
                {
                        "Che ruolo gioca l'auto-studio?",
                        "Non necessario",
                        "Aiuta a migliorare conoscenze e competenze",
                        "Solo per persone intelligenti",
                        "Crea pressione",
                        "Aiuta a migliorare conoscenze e competenze"
                },
                {
                        "Cosa aiuta a formare l'educazione civica?",
                        "Senso di responsabilità sociale",
                        "Solo conoscenza legale",
                        "Competenze professionali",
                        "Forza fisica",
                        "Senso di responsabilità sociale"
                },
                {
                        "Cosa significa 'l'apprendimento va di pari passo con la pratica'?",
                        "Solo apprendimento teorico",
                        "Combinare teoria con pratica",
                        "Solo pratica",
                        "Non c'è bisogno di studiare",
                        "Combinare teoria con pratica"
                },
                {
                        "Cosa significa uguaglianza educativa?",
                        "Tutti hanno accesso all'educazione",
                        "Solo le persone intelligenti possono studiare",
                        "Solo le persone ricche possono studiare",
                        "Nessuna discriminazione di genere",
                        "Tutti hanno accesso all'educazione"
                },
                {
                        "Che ruolo gioca l'educazione per la società?",
                        "Non importante",
                        "Sviluppo delle risorse umane",
                        "Serve solo gli individui",
                        "Serve solo l'economia",
                        "Sviluppo delle risorse umane"
                },
                {
                        "In cosa l'apprendimento aiuta gli esseri umani?",
                        "Aumentare il reddito",
                        "Espandere la comprensione",
                        "Sviluppo personale",
                        "Tutte le opzioni sopra",
                        "Tutte le opzioni sopra"
                },
                {
                        "Su cosa si concentra l'educazione moderna?",
                        "Memorizzazione",
                        "Pensiero creativo",
                        "Test",
                        "Disciplina rigida",
                        "Pensiero creativo"
                },
                {
                        "In cosa aiuta la tecnologia nell'educazione?",
                        "Causa distrazione",
                        "Migliora l'efficacia dell'apprendimento",
                        "Riduce la qualità",
                        "Non necessario",
                        "Migliora l'efficacia dell'apprendimento"
                },
                {
                        "Qual è la responsabilità degli studenti nell'apprendimento?",
                        "Studio superficiale",
                        "Proattivo e consapevole",
                        "Solo ascoltare lezioni",
                        "Dipendere da altri",
                        "Proattivo e consapevole"
                },
                {
                        "Qual è il ruolo più importante dell'educazione?",
                        "Trasmettere conoscenza",
                        "Formare individui completi",
                        "Test",
                        "Classificazione",
                        "Formare individui completi"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Thổ Nhĩ Kỳ
    private String[][] educationTurkish(){
        return new String[][] {
                {
                        "Eğitim nedir?",
                        "Eğlence etkinliği",
                        "Bilgi ve becerilerin aktarımı süreci",
                        "İş etkinliği",
                        "Dinlenme etkinliği",
                        "Bilgi ve becerilerin aktarımı süreci"
                },
                {
                        "Eğitimin temel amacı nedir?",
                        "Baskı yaratmak",
                        "Kapsamlı insan gelişimi",
                        "Sadece bilgi aktarmak",
                        "Sadece disiplin eğitimi",
                        "Kapsamlı insan gelişimi"
                },
                {
                        "Eğitim insanların neyi geliştirmesine yardımcı olur?",
                        "Fiziksel yönler",
                        "Zeka",
                        "Ahlak",
                        "Yukarıdaki tüm faktörler",
                        "Yukarıdaki tüm faktörler"
                },
                {
                        "Eğitim nereden başlar?",
                        "Okul",
                        "Aile",
                        "Toplum",
                        "Aile ve okul",
                        "Aile ve okul"
                },
                {
                        "Yaşam boyu öğrenmenin anlamı nedir?",
                        "Gerekli değil",
                        "Sosyal değişikliklere uyum sağlamaya yardımcı olur",
                        "Sadece öğrenciler için",
                        "Zaman kaybı",
                        "Sosyal değişikliklere uyum sağlamaya yardımcı olur"
                },
                {
                        "Öğretmenlerin rolü nedir?",
                        "Bilgi aktarımı ve rehberlik",
                        "Sadece not vermek",
                        "Öğrenci yönetimi",
                        "Ödev vermek",
                        "Bilgi aktarımı ve rehberlik"
                },
                {
                        "Öğrenmede öğrencilerin rolü nedir?",
                        "Pasif dinleme",
                        "Aktif öğrenme",
                        "Sadece sınavlar için çalışmak",
                        "Öğretmenlere bağımlı olmak",
                        "Aktif öğrenme"
                },
                {
                        "Ahlak eğitiminin amacı nedir?",
                        "Davranışı kontrol etmek",
                        "Karakter oluşturma",
                        "Baskı yaratmak",
                        "Sadece görgü kuralları öğretmek",
                        "Karakter oluşturma"
                },
                {
                        "Yaşam becerileri eğitimi öğrencilere neyle yardımcı olur?",
                        "Eğlence",
                        "Gerçek durumlarla başa çıkma",
                        "Sadece teorik öğrenme",
                        "Gerekli değil",
                        "Gerçek durumlarla başa çıkma"
                },
                {
                        "Eğitim ortamı nasıl etki eder?",
                        "Hiçbir etki yok",
                        "Öğrenme sonuçlarını etkiler",
                        "Sadece öğretmenleri etkiler",
                        "Sadece altyapıyı etkiler",
                        "Öğrenme sonuçlarını etkiler"
                },
                {
                        "Kendi kendine öğrenmenin rolü nedir?",
                        "Gerekli değil",
                        "Bilgi ve becerileri geliştirmeye yardımcı olur",
                        "Sadece zeki insanlar için",
                        "Baskı yaratır",
                        "Bilgi ve becerileri geliştirmeye yardımcı olur"
                },
                {
                        "Vatandaşlık eğitimi neyin oluşmasına yardımcı olur?",
                        "Sosyal sorumluluk duygusu",
                        "Sadece yasal bilgi",
                        "Mesleki beceriler",
                        "Fiziksel güç",
                        "Sosyal sorumluluk duygusu"
                },
                {
                        "'Öğrenme uygulama ile el ele gider' ne anlama gelir?",
                        "Sadece teorik öğrenme",
                        "Teoriyi uygulama ile birleştirmek",
                        "Sadece uygulama",
                        "Çalışmaya gerek yok",
                        "Teoriyi uygulama ile birleştirmek"
                },
                {
                        "Eğitimde eşitlik ne anlama gelir?",
                        "Herkesin eğitime erişimi var",
                        "Sadece zeki insanlar okuyabilir",
                        "Sadece zengin insanlar okuyabilir",
                        "Cinsiyet ayrımcılığı yok",
                        "Herkesin eğitime erişimi var"
                },
                {
                        "Eğitim toplum için ne rol oynar?",
                        "Önemli değil",
                        "İnsan kaynaklarını geliştirme",
                        "Sadece bireylere hizmet eder",
                        "Sadece ekonomiye hizmet eder",
                        "İnsan kaynaklarını geliştirme"
                },
                {
                        "Öğrenme insanlara neyle yardımcı olur?",
                        "Geliri artırmak",
                        "Anlayışı genişletmek",
                        "Kişisel gelişim",
                        "Yukarıdakilerin hepsi",
                        "Yukarıdakilerin hepsi"
                },
                {
                        "Modern eğitim neye odaklanır?",
                        "Ezberleme",
                        "Yaratıcı düşünce",
                        "Sınavlar",
                        "Katı disiplin",
                        "Yaratıcı düşünce"
                },
                {
                        "Eğitimde teknoloji neye yardımcı olur?",
                        "Dikkat dağınıklığına neden olur",
                        "Öğrenme etkinliğini artırır",
                        "Kaliteyi düşürür",
                        "Gerekli değil",
                        "Öğrenme etkinliğini artırır"
                },
                {
                        "Öğrenmede öğrencilerin sorumluluğu nedir?",
                        "Yüzeysel çalışma",
                        "Proaktif ve bilinçli",
                        "Sadece dersleri dinlemek",
                        "Başkalarına bağımlı olmak",
                        "Proaktif ve bilinçli"
                },
                {
                        "Eğitimin en önemli rolü nedir?",
                        "Bilgi aktarımı",
                        "Çok yönlü bireyler yetiştirmek",
                        "Sınavlar",
                        "Sıralama",
                        "Çok yönlü bireyler yetiştirmek"
                }
        };
    }

    // Hàm trả về câu hỏi về giáo dục tiếng Thái
    private String[][] educationThai(){
        return new String[][] {
                {
                        "การศึกษาคืออะไร?",
                        "กิจกรรมความบันเทิง",
                        "กระบวนการถ่ายทอดความรู้และทักษะ",
                        "กิจกรรมทางธุรกิจ",
                        "กิจกรรมพักผ่อน",
                        "กระบวนการถ่ายทอดความรู้และทักษะ"
                },
                {
                        "เป้าหมายหลักของการศึกษาคืออะไร?",
                        "สร้างความกดดัน",
                        "การพัฒนามนุษย์อย่างครอบคลุม",
                        "ถ่ายทอดความรู้เท่านั้น",
                        "ฝึกวินัยเท่านั้น",
                        "การพัฒนามนุษย์อย่างครอบคลุม"
                },
                {
                        "การศึกษาช่วยให้มนุษย์พัฒนาอะไร?",
                        "ด้านร่างกาย",
                        "สติปัญญา",
                        "คุณธรรม",
                        "ปัจจัยทั้งหมดข้างต้น",
                        "ปัจจัยทั้งหมดข้างต้น"
                },
                {
                        "การศึกษาเริ่มต้นจากที่ไหน?",
                        "โรงเรียน",
                        "ครอบครัว",
                        "สังคม",
                        "ครอบครัวและโรงเรียน",
                        "ครอบครัวและโรงเรียน"
                },
                {
                        "การเรียนรู้ตลอดชีวิตมีความหมายอย่างไร?",
                        "ไม่จำเป็น",
                        "ช่วยปรับตัวกับการเปลี่ยนแปลงทางสังคม",
                        "สำหรับนักเรียนเท่านั้น",
                        "เสียเวลา",
                        "ช่วยปรับตัวกับการเปลี่ยนแปลงทางสังคม"
                },
                {
                        "บทบาทของครูคืออะไร?",
                        "ถ่ายทอดความรู้และให้คำแนะนำ",
                        "ให้คะแนนเท่านั้น",
                        "จัดการนักเรียน",
                        "มอบหมายงาน",
                        "ถ่ายทอดความรู้และให้คำแนะนำ"
                },
                {
                        "บทบาทของนักเรียนในการเรียนรู้คืออะไร?",
                        "ฟังอย่างเฉยเมย",
                        "เรียนรู้อย่างกระตือรือร้น",
                        "เรียนเพื่อสอบเท่านั้น",
                        "พึ่งพาครู",
                        "เรียนรู้อย่างกระตือรือร้น"
                },
                {
                        "จุดมุ่งหมายของการศึกษาคุณธรรมคืออะไร?",
                        "ควบคุมพฤติกรรม",
                        "การสร้างบุคลิกภาพ",
                        "สร้างความกดดัน",
                        "สอนมารยาทเท่านั้น",
                        "การสร้างบุคลิกภาพ"
                },
                {
                        "การศึกษาทักษะชีวิตช่วยนักเรียนในเรื่องอะไร?",
                        "ความบันเทิง",
                        "รับมือกับสถานการณ์จริง",
                        "เรียนทฤษฎีเท่านั้น",
                        "ไม่จำเป็น",
                        "รับมือกับสถานการณ์จริง"
                },
                {
                        "สภาพแวดล้อมทางการศึกษามีอิทธิพลอย่างไร?",
                        "ไม่มีอิทธิพล",
                        "ส่งผลต่อผลการเรียนรู้",
                        "ส่งผลต่อครูเท่านั้น",
                        "ส่งผลต่อโครงสร้างพื้นฐานเท่านั้น",
                        "ส่งผลต่อผลการเรียนรู้"
                },
                {
                        "การเรียนรู้ด้วยตนเองมีบทบาทอย่างไร?",
                        "ไม่จำเป็น",
                        "ช่วยปรับปรุงความรู้และทักษะ",
                        "สำหรับคนฉลาดเท่านั้น",
                        "สร้างความกดดัน",
                        "ช่วยปรับปรุงความรู้และทักษะ"
                },
                {
                        "การศึกษาพลเมืองช่วยสร้างอะไร?",
                        "ความรู้สึกรับผิดชอบต่อสังคม",
                        "ความรู้ทางกฎหมายเท่านั้น",
                        "ทักษะอาชีพ",
                        "ความแข็งแรงทางกาย",
                        "ความรู้สึกรับผิดชอบต่อสังคม"
                },
                {
                        "'การเรียนรู้ไปด้วยกันกับการปฏิบัติ' หมายความว่าอย่างไร?",
                        "เรียนทฤษฎีเท่านั้น",
                        "ผสมผสานทฤษฎีกับการปฏิบัติ",
                        "ปฏิบัติเท่านั้น",
                        "ไม่จำเป็นต้องเรียน",
                        "ผสมผสานทฤษฎีกับการปฏิบัติ"
                },
                {
                        "ความเท่าเทียมทางการศึกษาหมายความว่าอย่างไร?",
                        "ทุกคนเข้าถึงการศึกษาได้",
                        "คนฉลาดเท่านั้นที่เรียนได้",
                        "คนรวยเท่านั้นที่เรียนได้",
                        "ไม่เลือกปฏิบัติเรื่องเพศ",
                        "ทุกคนเข้าถึงการศึกษาได้"
                },
                {
                        "การศึกษามีบทบาทอย่างไรต่อสังคม?",
                        "ไม่สำคัญ",
                        "พัฒนาทรัพยากรมนุษย์",
                        "ให้บริการแต่ปัจเจกบุคคล",
                        "ให้บริการแต่เศรษฐกิจ",
                        "พัฒนาทรัพยากรมนุษย์"
                },
                {
                        "การเรียนรู้ช่วยมนุษย์ในเรื่องอะไร?",
                        "เพิ่มรายได้",
                        "ขยายความเข้าใจ",
                        "การพัฒนาส่วนตัว",
                        "ทั้งหมดข้างต้น",
                        "ทั้งหมดข้างต้น"
                },
                {
                        "การศึกษาสมัยใหม่มุ่งเน้นอะไร?",
                        "การท่องจำ",
                        "การคิดเชิงสร้างสรรค์",
                        "การสอบ",
                        "วินัยที่เข้มงวด",
                        "การคิดเชิงสร้างสรรค์"
                },
                {
                        "เทคโนโลยีในการศึกษาช่วยอะไร?",
                        "ทำให้เสียสมาธิ",
                        "เพิ่มประสิทธิภาพการเรียนรู้",
                        "ลดคุณภาพ",
                        "ไม่จำเป็น",
                        "เพิ่มประสิทธิภาพการเรียนรู้"
                },
                {
                        "ความรับผิดชอบของนักเรียนในการเรียนรู้คืออะไร?",
                        "เรียนแบบผิวเผิน",
                        "เชิงรุกและมีสติ",
                        "ฟังบรรยายเท่านั้น",
                        "พึ่งพาผู้อื่น",
                        "เชิงรุกและมีสติ"
                },
                {
                        "บทบาทที่สำคัญที่สุดของการศึกษาคืออะไร?",
                        "ถ่ายทอดความรู้",
                        "สร้างบุคคลที่สมบูรณ์",
                        "การสอบ",
                        "การจัดอันดับ",
                        "สร้างบุคคลที่สมบูรณ์"
                }
        };
    }

    private String[][] musicEnglish(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicVN(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicSpanish(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicFrench(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicGerman(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicChinese(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicKorean(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicRussian(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicHindi(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicJapanese(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicPortuguese(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicDutch(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicArabic(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicItalian(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicTurkish(){
        String[][] res = null;
        return  res;
    }
    private String[][] musicThai(){
        String[][] res = null;
        return  res;
    }

    private String[][] entertainmentEnglish(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentVN(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentSpanish(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentFrench(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentGerman(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentChinese(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentKorean(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentRussian(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentHindi(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentJapanese(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentPortuguese(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentDutch(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentArabic(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentItalian(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentTurkish(){
        String[][] res = null;
        return  res;
    }
    private String[][] entertainmentThai(){
        String[][] res = null;
        return  res;
    }

    private String[][] getWorkQuestionsByLanguage(Long languageId) {
        switch (languageId.intValue()) {
            case 1: return workEnglish();
            case 2: return workVN();
            case 3: return workSpanish();
            case 4: return workFrench();
            case 5: return workGerman();
            case 6: return workChinese();
            case 7: return workJapanese();
            case 8: return workKorean();
            case 9: return workHindi();
            case 10: return workRussian();
            case 11: return workPortuguese();
            case 12: return workArabic();
            case 13: return workItalian();
            case 14: return workTurkish();
            case 15: return workDutch();
            case 16: return workThai();
            default: return workVN(); // Default to Vietnamese
        }
    }
    private String[][] getEnterQuestionsByLanguage(Long languageId) {
        switch (languageId.intValue()) {
            case 1: return entertainmentEnglish();
            case 2: return entertainmentVN();
            case 3: return entertainmentSpanish();
            case 4: return entertainmentFrench();
            case 5: return entertainmentGerman();
            case 6: return entertainmentChinese();
            case 7: return entertainmentJapanese();
            case 8: return entertainmentKorean();
            case 9: return entertainmentHindi();
            case 10: return entertainmentRussian();
            case 11: return entertainmentPortuguese();
            case 12: return entertainmentArabic();
            case 13: return entertainmentItalian();
            case 14: return entertainmentTurkish();
            case 15: return entertainmentDutch();
            case 16: return entertainmentThai();
            default: return entertainmentVN(); // Default to Vietnamese
        }
    }
    private String[][] getEduQuestionsByLanguage(Long languageId) {
        switch (languageId.intValue()) {
            case 1: return educationEnglish();
            case 2: return educationVN();
            case 3: return educationSpanish();
            case 4: return educationFrench();
            case 5: return educationGerman();
            case 6: return educationChinese();
            case 7: return educationJapanese();
            case 8: return educationKorean();
            case 9: return educationHindi();
            case 10: return educationRussian();
            case 11: return educationPortuguese();
            case 12: return educationArabic();
            case 13: return educationItalian();
            case 14: return educationTurkish();
            case 15: return educationDutch();
            case 16: return educationThai();
            default: return educationVN(); // Default to Vietnamese
        }
    }
    private String[][] getMusicQuestionsByLanguage(Long languageId) {
        switch (languageId.intValue()) {
            case 1: return musicEnglish();
            case 2: return musicVN();
            case 3: return musicSpanish();
            case 4: return musicFrench();
            case 5: return musicGerman();
            case 6: return musicChinese();
            case 7: return musicJapanese();
            case 8: return musicKorean();
            case 9: return musicHindi();
            case 10: return musicRussian();
            case 11: return musicPortuguese();
            case 12: return musicArabic();
            case 13: return musicItalian();
            case 14: return musicTurkish();
            case 15: return musicDutch();
            case 16: return musicThai();
            default: return musicVN(); // Default to Vietnamese
        }
    }


    private String[][] familyVN() {
        String[][] res = {
                {
                        "Gia đình là gì?",
                        "Nhóm người sống chung một nơi",
                        "Nhóm người có quan hệ bạn bè",
                        "Tập hợp người gắn bó bằng hôn nhân, huyết thống hoặc nuôi dưỡng",
                        "Tổ chức xã hội tự do",
                        "Tập hợp người gắn bó bằng hôn nhân, huyết thống hoặc nuôi dưỡng"
                },
                {
                        "Gia đình được hình thành chủ yếu dựa trên mối quan hệ nào?",
                        "Quan hệ kinh tế",
                        "Quan hệ huyết thống, hôn nhân, nuôi dưỡng",
                        "Quan hệ xã hội",
                        "Quan hệ bạn bè",
                        "Quan hệ huyết thống, hôn nhân, nuôi dưỡng"
                },
                {
                        "Vì sao gia đình được coi là tế bào của xã hội?",
                        "Vì gia đình đông người",
                        "Vì gia đình tồn tại lâu đời",
                        "Vì gia đình là nền tảng hình thành xã hội",
                        "Vì gia đình quản lý xã hội",
                        "Vì gia đình là nền tảng hình thành xã hội"
                },
                {
                        "Chức năng nào sau đây KHÔNG phải của gia đình?",
                        "Chức năng sinh sản",
                        "Chức năng giáo dục",
                        "Chức năng kinh tế",
                        "Chức năng quân sự",
                        "Chức năng quân sự"
                },
                {
                        "Chức năng giáo dục của gia đình thể hiện ở đâu?",
                        "Dạy kiến thức khoa học",
                        "Hình thành nhân cách, đạo đức",
                        "Quản lý nhà nước",
                        "Phát triển công nghệ",
                        "Hình thành nhân cách, đạo đức"
                },
                {
                        "Vai trò quan trọng nhất của gia đình đối với trẻ em là gì?",
                        "Cung cấp tiền bạc",
                        "Giáo dục và chăm sóc",
                        "Quản lý thời gian",
                        "Kiểm soát hành vi",
                        "Giáo dục và chăm sóc"
                },
                {
                        "Gia đình hạnh phúc là gia đình có đặc điểm nào?",
                        "Giàu có",
                        "Đông con",
                        "Các thành viên yêu thương, tôn trọng nhau",
                        "Có địa vị xã hội cao",
                        "Các thành viên yêu thương, tôn trọng nhau"
                },
                {
                        "Yếu tố nào ảnh hưởng trực tiếp đến hạnh phúc gia đình?",
                        "Sự quan tâm và chia sẻ",
                        "Số lượng thành viên",
                        "Diện tích nhà ở",
                        "Nghề nghiệp",
                        "Sự quan tâm và chia sẻ"
                },
                {
                        "Trách nhiệm của cha mẹ đối với con cái là gì?",
                        "Kiểm soát mọi hành vi",
                        "Nuôi dưỡng, giáo dục và bảo vệ",
                        "Chỉ cung cấp tài chính",
                        "Quyết định thay con",
                        "Nuôi dưỡng, giáo dục và bảo vệ"
                },
                {
                        "Trách nhiệm của con cái đối với cha mẹ là gì?",
                        "Nghe lời tuyệt đối",
                        "Phụ thuộc hoàn toàn",
                        "Kính trọng, hiếu thảo",
                        "Quyết định thay cha mẹ",
                        "Kính trọng, hiếu thảo"
                },
                {
                        "Gia đình truyền thống thường có đặc điểm nào?",
                        "Ít thế hệ",
                        "Quan hệ bình đẳng tuyệt đối",
                        "Nhiều thế hệ cùng chung sống",
                        "Không có vai trò cha mẹ",
                        "Nhiều thế hệ cùng chung sống"
                },
                {
                        "Gia đình hiện đại có đặc điểm nào?",
                        "Nhiều thế hệ",
                        "Quan hệ áp đặt",
                        "Bình đẳng, dân chủ hơn",
                        "Phụ thuộc họ hàng",
                        "Bình đẳng, dân chủ hơn"
                },
                {
                        "Mối quan hệ trong gia đình cần dựa trên nguyên tắc nào?",
                        "Áp đặt",
                        "Lợi ích cá nhân",
                        "Yêu thương, tôn trọng",
                        "Quyền lực",
                        "Yêu thương, tôn trọng"
                },
                {
                        "Nguyên nhân phổ biến dẫn đến mâu thuẫn gia đình là gì?",
                        "Thiếu giao tiếp, chia sẻ",
                        "Nhà ở chật",
                        "Đông con",
                        "Khác nghề nghiệp",
                        "Thiếu giao tiếp, chia sẻ"
                },
                {
                        "Gia đình có vai trò gì trong việc giữ gìn văn hóa?",
                        "Phát minh văn hóa",
                        "Truyền lại giá trị, truyền thống",
                        "Quản lý xã hội",
                        "Kiểm soát con người",
                        "Truyền lại giá trị, truyền thống"
                },
                {
                        "Gia đình ảnh hưởng đến nhân cách cá nhân thông qua yếu tố nào?",
                        "Môi trường sống và giáo dục",
                        "Tiền bạc",
                        "Địa vị xã hội",
                        "Quyền lực",
                        "Môi trường sống và giáo dục"
                },
                {
                        "Trách nhiệm chung của các thành viên trong gia đình là gì?",
                        "Chỉ lo cho bản thân",
                        "Chia sẻ, giúp đỡ lẫn nhau",
                        "Phụ thuộc người khác",
                        "Tránh trách nhiệm",
                        "Chia sẻ, giúp đỡ lẫn nhau"
                },
                {
                        "Gia đình có vai trò gì đối với xã hội hiện đại?",
                        "Không còn quan trọng",
                        "Chỉ mang tính cá nhân",
                        "Là nền tảng ổn định xã hội",
                        "Chỉ phục vụ kinh tế",
                        "Là nền tảng ổn định xã hội"
                },
                {
                        "Biểu hiện của một gia đình hạnh phúc là gì?",
                        "Giàu có",
                        "Ít mâu thuẫn",
                        "Yêu thương, gắn bó",
                        "Có nhiều tài sản",
                        "Yêu thương, gắn bó"
                },
                {
                        "Điều quan trọng nhất để xây dựng gia đình hạnh phúc là gì?",
                        "Tiền bạc",
                        "Quyền lực",
                        "Sự yêu thương và tôn trọng",
                        "Địa vị xã hội",
                        "Sự yêu thương và tôn trọng"
                }
        };
        return res;
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

    private void seedTestFor(LearningLanguage learningLanguage, TopicTest topic, String[][] questions) throws Exception {
        var easy = difficultyTestRepository.findById(1L).get();
        var medium = difficultyTestRepository.findById(2L).get();
        var hard = difficultyTestRepository.findById(3L).get();

        var test1 = Test.builder()
                .title(topic.getName())
                .subTitle("Basic questions about topic: " + topic.getName())
                .difficultyTests(easy)
                .topicTest(topic)
                .learningLanguage(learningLanguage)
                .build();
        var test2 = Test.builder()
                .title(topic.getName())
                .subTitle("Medium questions about topic: " + topic.getName())
                .difficultyTests(medium)
                .topicTest(topic)
                .learningLanguage(learningLanguage)
                .build();
        var test3 = Test.builder()
                .title(topic.getName())
                .subTitle("Advanced questions about topic: " + topic.getName())
                .difficultyTests(hard)
                .topicTest(topic)
                .learningLanguage(learningLanguage)
                .build();
        testRepository.saveAll(List.of(test1, test2, test3));

        seedForTest(test1, questions);
        seedForTest(test2, questions);
        seedForTest(test3, questions);
    }

    public void seedForTest(Test test, String[][] questions) {
        for (int i = 0; i < questions.length; i++) {
            var question = questions[i];
            var contentQuestion = question[0];
            var contentOpt1 = question[1];
            var contentOpt2 = question[2];
            var contentOpt3 = question[3];
            var contentOpt4 = question[4];
            var contentAnswer = question[5];
            var quest = QuestionTests.builder()
                    .orderNumber((i + 1) * 1L)
                    .content(contentQuestion)
                    .test(test)
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
