/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.config;


import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.zip.CRC32;

import javax.net.ssl.KeyManagerFactory;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.saml2.metadata.provider.FileBackedHTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.ws.soap.client.http.HttpClientBuilder;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.signature.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.storage.EmptyStorageFactory;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.w3c.dom.Document;

import it.regioneveneto.myp3.clients.common.models.ProxyModel;
import it.regioneveneto.myp3.myextranet.security.FakeUserDetailsService;
import it.regioneveneto.myp3.myextranet.security.JwtAuthenticationEntryPoint;
import it.regioneveneto.myp3.myextranet.security.JwtRequestFilter;
import it.regioneveneto.myp3.myextranet.security.JwtTokenUtil;
import it.regioneveneto.myp3.myextranet.security.SAMLUserDetailsServiceImpl;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.security.util.SpringResourceWrapperOpenSAMLResource;
import it.regioneveneto.myp3.myextranet.security.util.ssl.EasySSLProtocolSocketFactory;
import it.regioneveneto.myp3.myextranet.utils.Constants;


@Configuration
@ComponentScan("it.regioneveneto.myp3.myextranet")
@EnableWebMvc
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "urn:oasis:names:tc:SAML:2.0:ac:classes:SecureRemotePassword";
    private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT_SSO = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    private static final String SAML2_PASSWORD_SMARTCARD_SSO = "urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard";


    private static final String[] SECURITY_WHITELIST = { "/favicon.ico" }; 
    private static final String[] AUTH_WHITELIST = {"/public/**", "/ws/**", "/saml/**", "/actuator/health/liveness/**", "/actuator/health/readiness/**"};

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);


    @Value("${cors.enabled:false}")
    private String corsEnabled;
    @Value("${auth.fake.enabled:false}")
    private String fakeAuthEnabled;

    @Value("${static.serve.enabled:false}")
    private String staticContentEnabled;
    @Value("${static.serve.path:/staticContent}")
    private String staticContentPath;
    @Value("${static.serve.location:/staticLocation}")
    private String staticContentLocation;

    @Value("${server.error.path:/error}")
    private String errorPath;
    
    // SAML

    @Value("${saml.proxy.enabled:false}")
    private boolean samlProxyEnabled;
    @Value("${saml.proxy.scheme}")
    private String samlProxyScheme;
    @Value("${saml.proxy.server-name}")
    private String samlProxyServerName;
    @Value("${saml.proxy.server-port}")
    private int samlProxyServerPort;
    @Value("${saml.proxy.include-port}")
    private boolean samlProxyIncludePort;
    @Value("${saml.proxy.context-path}")
    private String samlProxyContextPath;

    @Value("${saml.key-store}")
    private String samlKeystore;
    @Value("${saml.key-store-password}")
    private String samlKeystorePassword;
    @Value("${saml.key-alias}")
    private String samlAlias;
    @Value("${saml.key-password}")
    private String samlAliasPassword;

    @Value("${saml.app-base-url}")
    private String samlAppBaseUrl;
    @Value("${saml.app-entity-id}")
    private String samlAppEntityId;

    @Value("${saml.app-metadata-file}")
    private String samlAppMetadataFile;
    @Value("${saml.idp-selection-path}")
    private String setIdpSelectionPath;
    
    @Value("${saml.idp-metadata-url:}")
    private String samlIdpMetadataUrl;
    @Value("${saml.idp-metadata-https-cert:}")
    private String samlIdpMetadataHttpsCert;
    @Value("${saml.idp-metadata-resource:}")
    private String samlIdpMetadataResource;

    @Value("${saml.failure-url}")
    private String samlFailureUrl;
    @Value("${saml.logout-url}")
    private String samlLogoutUrl;

    @Value("${saml.entry.logout}")
    private String samlEntrypointLogout;
    @Value("${saml.entry.metadata}")
    private String samlEntrypointMetadata;
    @Value("${saml.entry.login}")
    private String samlEntrypointLogin;
    @Value("${saml.entry.SSO}")
    private String samlEntrypointSSO;
    @Value("${saml.entry.SSOHoK}")
    private String samlEntrypointSSOHoK;
    @Value("${saml.entry.SingleLogout}")
    private String samlEntrypointSingleLogout;
    @Value("${saml.entry.discovery}")
    private String samlEntrypointDiscovery;

    @Value("${jwt.validity.seconds:36000}") //default: 10 hours
    private long jwtTokenValidity;

    @Value("${cookie.httpOnly:true}")
    private boolean httpOnly;
    
    @Autowired
    private SAMLUserDetailsServiceImpl samlUserDetailsServiceImpl;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private FakeUserDetailsService fakeUserDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
	private CacheManager cacheManager;


    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
      MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
      methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
      methodInvokingFactoryBean.setTargetMethod("setStrategyName");
      methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
      return methodInvokingFactoryBean;
    }

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Bean
    public PasswordEncoder passwordEncoder() {
      return this.passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider usernamePasswordAuthenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(fakeUserDetailsService);
      provider.setPasswordEncoder(passwordEncoder());
      return provider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) {
      String[] securityWhitelist = ArrayUtils.addAll(SECURITY_WHITELIST, errorPath);
      if("true".equalsIgnoreCase(staticContentEnabled)) {
        logger.warn("serving static content at path: " + staticContentPath);
        securityWhitelist = ArrayUtils.addAll(securityWhitelist, staticContentPath, staticContentPath + "/**");
      }
      web.ignoring().antMatchers(securityWhitelist);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
      if("true".equalsIgnoreCase(corsEnabled)) {
        logger.warn("enabling CORS (security)");
        httpSecurity = httpSecurity.cors().and();
      }
      httpSecurity.csrf().disable()
          .authorizeRequests()
          .antMatchers(ArrayUtils.addAll(AUTH_WHITELIST)).permitAll()
          .anyRequest().authenticated().and()
          // make sure we use stateless session; session won't be used to store user's state.
          .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // Add a filter to validate the tokens with every request
      httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      if("true".equalsIgnoreCase(corsEnabled)) {
        logger.warn("enabling CORS");
        registry.addMapping("/**");
      }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
  	  logger.warn("enabling staticContentPath {}", staticContentPath);
  	  logger.warn("enabling staticContentLocation {}", staticContentLocation);
      if("true".equalsIgnoreCase(staticContentEnabled)) {

        registry
            .addResourceHandler(staticContentPath + "/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(false)
            .addResolver(new PathResourceResolver() {
              @Override
              protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
                Resource resource = super.resolveResourceInternal(request, requestPath, locations, chain);
                if (resource == null) {
                  resource = super.resolveResourceInternal(request, "index.html", locations, chain);
                }
                return resource;
              }
            });
      }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
      if("true".equalsIgnoreCase(fakeAuthEnabled))
        auth.authenticationProvider(usernamePasswordAuthenticationProvider());
      else
        //saml provider
        auth.authenticationProvider(samlAuthenticationProvider());
    }
    
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
      return new SecurityContextLogoutHandler();
    }
    
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    protected SessionRegistry sessionRegistryImpl() {
      return new SessionRegistryImpl();
    }

    // SAML being XML based protocol, XML parser pools should be initialized to read
    // metadata and assertions that are in XML format.
    // Initialization of the velocity engine
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public VelocityEngine velocityEngine() {
      return VelocityFactory.getEngine();
    }

    // XML parser pool needed for OpenSAML parsing
    @Bean(initMethod = "initialize")
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public StaticBasicParserPool parserPool() {
      return new StaticBasicParserPool();
    }

    @Bean(name = "parserPoolHolder")
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public ParserPoolHolder parserPoolHolder() {
      return new ParserPoolHolder();
    }

    // Bindings, encoders and decoders used for creating and parsing messages
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
      return new MultiThreadedHttpConnectionManager();
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HttpClient httpClient() {
      return new HttpClient(multiThreadedHttpConnectionManager());
    }

    // SAML Authentication Provider responsible for validating of received SAML
    // messages
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
      SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
      samlAuthenticationProvider.setUserDetails(samlUserDetailsServiceImpl);
      samlAuthenticationProvider.setForcePrincipalAsString(false);
      return samlAuthenticationProvider;
    }



    /**
     * Provider of default SAML Context This configuration is for the application that is not behind a Reverse Proxy. Alternatively,
     * SAMLContextProviderLB can be used, which is a Context provider that overrides request attributes with values of the load-balancer or
     * reverse-proxy in front of the local application. The settings help to provide correct redirect URls and verify destination URLs during SAML
     * processing.
     */
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLContextProvider contextProvider() {
      SAMLContextProvider provider;
      if(samlProxyEnabled) {
        SAMLContextProviderLB samlContextProviderLB = new SAMLContextProviderLB();
        samlContextProviderLB.setScheme(samlProxyScheme);
        samlContextProviderLB.setServerName(samlProxyServerName);
        samlContextProviderLB.setServerPort(samlProxyServerPort);
        samlContextProviderLB.setIncludeServerPortInRequestURL(samlProxyIncludePort);
        samlContextProviderLB.setContextPath(samlProxyContextPath);
        samlContextProviderLB.setStorageFactory(new EmptyStorageFactory());
        provider = samlContextProviderLB;
      } else {
        provider = new SAMLContextProviderImpl();
      }
      return provider;
    }

    // Initialization of OpenSAML library
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public static SAMLBootstrap samlBootstrap() {
      return new SAMLBootstrap(){
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
          super.postProcessBeanFactory(beanFactory);
          BasicSecurityConfiguration config = (BasicSecurityConfiguration) org.opensaml.xml.Configuration.getGlobalSecurityConfiguration();

          config.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
          config.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        }
      };
    }

    // Logger for SAML messages and events
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLDefaultLogger samlLogger() {
      return new SAMLDefaultLogger();
    }

    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfileConsumer webSSOprofileConsumer() {
      WebSSOProfileConsumerImpl sso = new WebSSOProfileConsumerImpl();
      sso.setMaxAssertionTime(72000);
      sso.setResponseSkew(600);
      return new WebSSOProfileConsumerImpl();
    }

    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
      return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 Web SSO profile
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfile webSSOprofile() {
      return new WebSSOProfileImpl();
    }

    // SAML 2.0 Holder-of-Key Web SSO profile
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
      return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 ECP profile
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfileECPImpl ecpprofile() {
      return new WebSSOProfileECPImpl();
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SingleLogoutProfile logoutprofile() {
      return new SingleLogoutProfileImpl();
    }

    /**
     * Metadata generation requires a keyManager, it is responsible to encrypt the saml assertion sent to IdP. A self-signed key and keystore can be
     * generated with the JRE keytool command: keytool -genkeypair -alias mykeyalias -keypass mykeypass -storepass samlstorepass -keystore
     * saml-keystore.jks
     */
    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public KeyManager keyManager() {
      DefaultResourceLoader loader = new DefaultResourceLoader();
      Resource storeFile = loader.getResource(samlKeystore);
      Map<String, String> passwords = new HashMap<>();
      passwords.put(samlAlias, samlAliasPassword);
      return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlAlias);
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {

      WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
      webSSOProfileOptions.setIncludeScoping(false);
      webSSOProfileOptions.setProxyCount(0);
      webSSOProfileOptions.setForceAuthN(true);
      webSSOProfileOptions.setNameID(NameIDType.TRANSIENT);

      webSSOProfileOptions.setAllowCreate(true);
      webSSOProfileOptions.setAuthnContextComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);
      webSSOProfileOptions.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

      Collection<String> contexts = new ArrayList<>();      
      contexts.add(SAML2_PASSWORD_PROTECTED_TRANSPORT_SSO);
      contexts.add(SAML2_PASSWORD_SMARTCARD_SSO);
      webSSOProfileOptions.setAuthnContexts(contexts);
      return webSSOProfileOptions;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLEntryPoint samlEntryPoint() {
      return new SAMLEntryPoint(){
        @Override
        protected WebSSOProfileOptions getProfileOptions(SAMLMessageContext context, AuthenticationException exception) {
          WebSSOProfileOptions defaultOptions = defaultWebSSOProfileOptions();
          String callbackUrl = ((HttpServletRequestAdapter)context.getInboundMessageTransport()).getWrappedRequest().getParameter("callbackUrl");
          defaultOptions.setRelayState(callbackUrl);
          
          logger.info("#CALLBACK URL " + callbackUrl);

          return defaultOptions;
        }
      };
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLDiscovery samlIDPDiscovery() {
      SAMLDiscovery idpDiscovery = new SAMLDiscovery();
      idpDiscovery.setIdpSelectionPath(setIdpSelectionPath);
      return idpDiscovery;
    }

    @Bean
    @Qualifier("idp-ssocircle")
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public ExtendedMetadataDelegate ssoCircleExtendedMetadataProvider(ResourceLoader resourceLoader, ParserPool parserPool) throws MetadataProviderException, ResourceException {

        MetadataProvider provider;

        if(StringUtils.isNotBlank(samlIdpMetadataUrl)) {

          Timer taskTimer = new Timer("idpMetadataProviderTimer", true);

          KeyStore keyStore = null;
          javax.net.ssl.KeyManager[] keyManagers=null;
          if(StringUtils.isNotBlank(samlIdpMetadataHttpsCert))
            try {
              //create empty keystore
              keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
              char[] keyStorePass = UUID.randomUUID().toString().toCharArray();
              keyStore.load(null, keyStorePass);
              //load the trusted cert of MyID server
              CertificateFactory fact = CertificateFactory.getInstance("X.509");
              Resource myIdCertFile = resourceLoader.getResource(samlIdpMetadataHttpsCert);
              X509Certificate myIdCert = (X509Certificate) fact.generateCertificate(myIdCertFile.getInputStream());
              keyStore.setCertificateEntry("idp", myIdCert);
              KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
              factory.init(keyStore, keyStorePass);
              keyManagers = factory.getKeyManagers();
            } catch (Exception e){
              throw new ResourceException("error loading MyID server certificate",e);
            }
          
          
          HttpClientBuilder builder = new HttpClientBuilder();

          setProxyInfoToBuilder(builder, samlIdpMetadataUrl);
          
          builder.setHttpsProtocolSocketFactory(new EasySSLProtocolSocketFactory(keyStore, keyManagers));
          builder.setConnectionTimeout(10000); //timeout: 10s
          HttpClient httpClient = builder.buildClient();

          String urlHash;
          try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(samlIdpMetadataUrl.getBytes());
            byte[] digest = md.digest();
            urlHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
          } catch (NoSuchAlgorithmException e){
            CRC32 crc = new CRC32();
            crc.update(samlIdpMetadataUrl.getBytes());
            urlHash = Long.toHexString(crc.getValue());
          }
          String backupPath = System.getProperty("java.io.tmpdir") + File.separator + urlHash + ".xml";
          logger.info("creating FileBackedHTTPMetadataProvider for IDP metadata, url: "+samlIdpMetadataUrl+" - pathBackup: "+backupPath);

          FileBackedHTTPMetadataProvider httpProvider = new FileBackedHTTPMetadataProvider(taskTimer, httpClient, samlIdpMetadataUrl, backupPath);
          httpProvider.setParserPool(parserPool);
          provider = httpProvider;

        } else if(StringUtils.isNotBlank(samlIdpMetadataResource)) {

          Resource storeFile = resourceLoader.getResource(samlIdpMetadataResource);
          Timer taskTimer = new Timer("idpMetadataProviderTimer", true);
          logger.info("creating ResourceBackedMetadataProvider for IDP metadata, resource: "+samlIdpMetadataResource);
          ResourceBackedMetadataProvider resourceProvider = new ResourceBackedMetadataProvider(taskTimer, new SpringResourceWrapperOpenSAMLResource(storeFile));
          resourceProvider.setParserPool(parserPool);
          provider = resourceProvider;

        } else {
          throw new MetadataProviderException("missing both params saml.idp-metadata-url and saml.idp-metadata-resource");
        }

        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(provider, extendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        return extendedMetadataDelegate;
      }

    @Bean
    @Qualifier("metadata")
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public CachingMetadataManager metadata(ResourceLoader resourceLoader, ParserPool parserPool) throws MetadataProviderException, ResourceException {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(ssoCircleExtendedMetadataProvider(resourceLoader, parserPool));
        providers.add(extendedMetadataDelegate());
        return new CachingMetadataManager(providers);
      }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public MetadataGenerator metadataGenerator() {
      MetadataGenerator metadataGenerator = new MetadataGenerator();

      // APP_ENTITY_ID – This is the name of the application/ audience field in the
      // application set-up for the IDP
      metadataGenerator.setEntityId(samlAppEntityId);
      // APP_BASE_URL –This is the application’s base url after deployment, it varies
      // according to the environment the application is deployed in.
      metadataGenerator.setEntityBaseURL(samlAppBaseUrl);
      metadataGenerator.setNameID(Collections.singletonList("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
      metadataGenerator.setExtendedMetadata(extendedMetadata());
      metadataGenerator.setIncludeDiscoveryExtension(false);
      metadataGenerator.setKeyManager(keyManager());
      return metadataGenerator;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public ExtendedMetadata extendedMetadata() {
      ExtendedMetadata extendedMetadata = new ExtendedMetadata();
      extendedMetadata.setIdpDiscoveryEnabled(false);
      extendedMetadata.setDigestMethodAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
      extendedMetadata.setSigningAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
      extendedMetadata.setSignMetadata(false);
      extendedMetadata.setRequireLogoutRequestSigned(false);

      return extendedMetadata;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public MetadataDisplayFilter metadataDisplayFilter() {
      return new MetadataDisplayFilter();
    }



    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public AuthenticationSuccessHandler successRedirectHandler() {
      return new AuthenticationSuccessHandler() {
        private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
          //redirect to callback url specified when initiating login procedure, passing JWT token as query param
          try {
        	final SAMLCredential credential = (SAMLCredential) authentication.getCredentials();
          Authentication innerAuth = (Authentication)authentication.getPrincipal();
          UserWithAdditionalInfo userDetails = (UserWithAdditionalInfo) innerAuth.getPrincipal();
          logger.info("principal: "+userDetails);
          Map<String, Object> claims = new HashMap<>();
          claims.put("cognome",userDetails.getCognome());
          claims.put("nome",userDetails.getNome());
          claims.put("codiceFiscale",userDetails.getCodiceFiscale());
          claims.put("email",userDetails.getEmail());
          final String token = jwtTokenUtil.generateToken(userDetails.getUsername(), claims);
         
          // store token as valid
          jwtTokenUtil.storeTokenInCache(token);
          
          // clear roles cache for user
          evictRolesFromCache(userDetails.getCodiceFiscale());
          
          // clear validity cache for user
          evictUserValidityFromCache(userDetails.getCodiceFiscale());
          
          //url encode token
          String targetUrl = credential.getRelayState();
          logger.debug("success authentication url: "+targetUrl);
          
          // set cookie
          Cookie cookie = new Cookie(Constants.COOKIE_NAME_ACCESS_TOKEN, token);
          cookie.setHttpOnly(httpOnly);
          cookie.setMaxAge((int) jwtTokenValidity);
          cookie.setSecure(httpOnly);
          cookie.setPath("/");
          response.addCookie(cookie);
          
          redirectStrategy.sendRedirect(request, response, targetUrl);
          } catch (Exception e) {
        	  logger.error("Cookie retrival error in AuthenticationSuccessHandler", e);
        	  throw e;
          }
        }
        
      };
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
      SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
      failureHandler.setUseForward(false);
      failureHandler.setDefaultFailureUrl(samlFailureUrl);
      return failureHandler;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
      SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
      samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
      samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager());
      samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
      return samlWebSSOHoKProcessingFilter;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {

      SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
      samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
      samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
      samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

      return samlWebSSOProcessingFilter;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public MetadataGeneratorFilter metadataGeneratorFilter() {
      return new MetadataGeneratorFilter(metadataGenerator());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
      SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
      successLogoutHandler.setDefaultTargetUrl(samlLogoutUrl);
      return successLogoutHandler;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SecurityContextLogoutHandler logoutHandler() {
      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
      logoutHandler.setInvalidateHttpSession(true);
      logoutHandler.setClearAuthentication(true);
      return logoutHandler;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
      return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLLogoutFilter samlLogoutFilter() {
      return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[]{logoutHandler()},
          new LogoutHandler[]{logoutHandler()});
    }

    private ArtifactResolutionProfile artifactResolutionProfile() {
      final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient());
      artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
      return artifactResolutionProfile;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
      return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPSOAP11Binding soapBinding() {
      return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPPostBinding httpPostBinding() {
      return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
      return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPSOAP11Binding httpSOAP11Binding() {
      return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public HTTPPAOS11Binding httpPAOS11Binding() {
      return new HTTPPAOS11Binding(parserPool());
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public SAMLProcessorImpl processor() {
      Collection<SAMLBinding> bindings = new ArrayList<>();
      bindings.add(httpRedirectDeflateBinding());
      bindings.add(httpPostBinding());
      bindings.add(artifactBinding(parserPool(), velocityEngine()));
      bindings.add(httpSOAP11Binding());
      bindings.add(httpPAOS11Binding());
      return new SAMLProcessorImpl(bindings);
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public FilterChainProxy samlFilter() throws Exception {

      List<SecurityFilterChain> chains = new ArrayList<>();

      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointLogout), samlLogoutFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointMetadata), metadataDisplayFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointLogin), samlEntryPoint()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointSSO), samlWebSSOProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointSSOHoK), samlWebSSOHoKProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointSingleLogout), samlLogoutProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher(samlEntrypointDiscovery), samlIDPDiscovery()));

      return new FilterChainProxy(chains);
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public ExtendedMetadataDelegate extendedMetadataDelegate() {

      AbstractMetadataProvider provider = new AbstractMetadataProvider() {
        @Override
        protected XMLObject doGetMetadata() throws MetadataProviderException {
        
        	MetadataGenerator mdg = metadataGenerator();
        	XMLObject ed =  mdg.generateMetadata();
        	return ed;
        }
      };

      ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(provider, spExtendedMetadata());
      extendedMetadataDelegate.setMetadataTrustCheck(false);
      extendedMetadataDelegate.setMetadataRequireSignature(false);
      return extendedMetadataDelegate;
    }

    @Bean
    @ConditionalOnProperty(name="auth.fake.enabled", havingValue="false")
    public ExtendedMetadata spExtendedMetadata() {
      ExtendedMetadata extendedMetadata = new ExtendedMetadata();
      extendedMetadata.setLocal(true);
      extendedMetadata.setSecurityProfile("metaiop");
      extendedMetadata.setSslSecurityProfile("pkix");
      extendedMetadata.setSignMetadata(false);
      extendedMetadata.setSigningKey(samlAlias);
      extendedMetadata.setRequireArtifactResolveSigned(false);
      extendedMetadata.setRequireLogoutRequestSigned(false);
      extendedMetadata.setRequireLogoutResponseSigned(false);
      extendedMetadata.setIdpDiscoveryEnabled(false);
      extendedMetadata.setSigningAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
      return extendedMetadata;
    }
    
	public void evictRolesFromCache(String code) {
    	logger.debug(String.format("Evicting roles from cache for code \"%s\"", code));
    	Cache cache = cacheManager.getCache(Constants.CACHE_NAME_USER_ROLES);
    	if (cache != null) {
    		cache.evict(code);
    		logger.debug(String.format("Roles for code \"%s\" evicted from cache \"%s\"", code, Constants.CACHE_NAME_USER_ROLES));
    	} else {
    		logger.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_USER_ROLES));
    	}
    }
	
	public void evictUserValidityFromCache(String code) {
    	logger.debug(String.format("Evicting validity from cache for code \"%s\"", code));
    	Cache cache = cacheManager.getCache(Constants.CACHE_NAME_USER_VALIDITY);
    	if (cache != null) {
    		cache.evict(code);
    		logger.debug(String.format("Validity for code \"%s\" evicted from cache \"%s\"", code, Constants.CACHE_NAME_USER_VALIDITY));
    	} else {
    		logger.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_USER_VALIDITY));
    	}
    }
	
	/**
     * Set Proxy info se settate nelle variabili d'ambiente
     * @param builder, httpclientbuilder con cui chiamare
     * @param url ,url da cui ricavare il proxy
     * @throws ResourceException se url malformed
     */
    private void setProxyInfoToBuilder(HttpClientBuilder builder, String url) throws ResourceException {
        try {
            ProxyModel proxyModel = new ProxyModel(url);
            
            Proxy proxy = proxyModel.getProxy();
			if (proxy != null && proxy.type() != Proxy.Type.DIRECT) {
	            String proxyHost = proxyModel.getProxyHost();
	            Integer proxyPort = proxyModel.getProxyPort();
	
	            if (StringUtils.isNotEmpty(proxyHost) && proxyPort != null) {
	                builder.setProxyHost(proxyHost);
	                builder.setProxyPort(proxyPort);
	            }
	
	            //Set authentication info if present
	            String proxyUsername = proxyModel.getProxyUsername();
	            if (StringUtils.isNotEmpty(proxyUsername)) {
	                builder.setProxyUsername(proxyUsername);
	            }
	
	            String proxyPassword = proxyModel.getProxyPassword();
	            if (StringUtils.isNotEmpty(proxyPassword)) {
	                builder.setProxyPassword(proxyPassword);
	            }
			}

        }  catch (URISyntaxException e) {
            throw new ResourceException("Error searching proxy from java system properties",e);
        }
    }
	
    
}
