package org.easy.data.biding.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.easy.data.biding.annotation.BindingParameter;

/***
 * 
 * @author joaobatista
 *
 */
public class BidingGenerator {

	private static Map<String, Map<String, Object>> mapProperties;

	/***
	 * Create a connection between the data form and the servlet.
	 * @param object
	 * @param request
	 */
	public static void generateBinding(Object object, HttpServletRequest request) {
		mapProperties = generatePropertiesMap(request);
		Class<?> classe = object.getClass();
		for (Method method : classe.getMethods()) {
			try {
				if (isGetterBiding(method)) {
					String propertie = fromGetterToPropertie(method.getName());
					Object value = method.getReturnType().newInstance();

					for (String prop : mapProperties.keySet()) {
						if (propertie.equals(prop)) {
							populate(value, propertie);
						}
					}
					set(object, propertie, value);
				}
			} catch (Exception e) {
				throw new RuntimeException("Problema ao gerar o mapa", e);
			}
		}
	}

	/**
	 * Verify is the mathod is a get method
	 * @param method
	 * @return
	 */
	private static boolean isGetter(Method method) {
		return method.getName().startsWith("get") && method.getReturnType() != void.class && method.getParameterTypes().length == 0;
	}

	/**
	 * Verify is the mathod is a get method and noted as @BindingParameter
	 * @param method
	 * @return
	 */
	private static boolean isGetterBiding(Method method) {
		return method.getName().startsWith("get") && method.getReturnType() != void.class && method.getParameterTypes().length == 0 && method.isAnnotationPresent(BindingParameter.class);
	}

	/**
	 * get attribute name from getter
	 * @param getterName
	 * @return
	 */
	private static String fromGetterToPropertie(String getterName) {
		StringBuffer retorno = new StringBuffer();
		retorno.append(getterName.substring(3, 4).toLowerCase());
		retorno.append(getterName.substring(4));
		return retorno.toString();
	}

	/**
	 * set value to attribute
	 * @param objeto
	 * @param fieldName
	 * @param valor
	 * @return
	 */
	public static boolean set(Object objeto, String fieldName, Object valor) {
		Class<?> clazz = objeto.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(objeto, valor);
				return true;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return false;
	}

	/**
	 * Generate a map properties to an object.
	 * @param request
	 * @return
	 */
	public static Map<String, Map<String, Object>> generatePropertiesMap(HttpServletRequest request) {
		Map<String, Map<String, Object>> mapa = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		Map<String, String[]> parametros = request.getParameterMap();

		for (String chave : parametros.keySet()) {
			String[] names = chave.split(Pattern.quote("."));
			properties.put(names[1], parametros.get(chave)[0]);
			mapa.put(names[0], properties);
		}
		return mapa;
	}

	/**
	 * Populate a Object
	 * @param object
	 * @param propertie
	 */
	public static void populate(Object object, String propertie) {
		Class<?> classe = object.getClass();
		for (Method method : classe.getMethods()) {
			if (isGetter(method)) {
				String prop = fromGetterToPropertie(method.getName());
				for (Object value : mapProperties.get(propertie).keySet()) {
					if (prop.equals(value)) {
						set(object, prop, mapProperties.get(propertie).get(value));
					}
				}
			}
		}
	}
}
