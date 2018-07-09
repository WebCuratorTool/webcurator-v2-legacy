package org.webcurator.core.permissionmapping;

import java.util.*;

import org.webcurator.domain.model.core.Permission;
import org.webcurator.domain.model.core.Seed;
import org.webcurator.domain.model.core.Site;
import org.webcurator.domain.model.core.Target;
import org.webcurator.domain.model.core.UrlPattern;
import org.webcurator.domain.*;

public class MockPermissionMappingStrategy extends PermissionMappingStrategy {

	Map<Permission, Set<UrlPattern>> mappings = new HashMap<Permission, Set<UrlPattern>>();
	TargetDAO targetDao = null;
	
	@Override
	public void add(Permission permission, UrlPattern urlPattern) {
		if(mappings.containsKey(permission))
		{
			Set<UrlPattern> patterns = mappings.get(permission);
			patterns.add(urlPattern);
		}
		else
		{
			Set<UrlPattern> patterns = new HashSet<UrlPattern>();
			patterns.add(urlPattern);
			mappings.put(permission, patterns);
		}
	}

	@Override
	public Set<Permission> getMatchingPermissions(Target target, Seed seed) {
		return seed.getPermissions();
	}

	@Override
	public Set<Permission> getMatchingPermissions(Target target, String url) {
		Set<Seed> seeds = targetDao.getSeeds(target);
		
		Iterator<Seed> it = seeds.iterator();
		while(it.hasNext())
		{
			Seed seed = it.next();
			if(url.equals(seed.getSeed()))
			{
				return seed.getPermissions();
			}
		}
		return new HashSet<Permission>();
	}

	@Override
	public boolean isValidPattern(UrlPattern urlPattern) {
		return true;
	}

	@Override
	public boolean matches(UrlPattern urlPattern, Seed seed) {
		return (urlPattern.getPattern().equals(seed.getSeed()));
	}

	@Override
	public void remove(Permission permission, UrlPattern urlPattern) {
		if(mappings.containsKey(permission))
		{
			Set<UrlPattern> patterns = mappings.get(permission);
			if(patterns.contains(urlPattern))
			{
				patterns.remove(urlPattern);
			}
		}
	}

	@Override
	public void removeMappings(Site site) {
		
		Set<Permission> permissions = site.getRemovedPermissions();
		Iterator<Permission> it = permissions.iterator();
		while(it.hasNext())
		{
			Permission p = it.next();
			if(mappings.containsKey(p))
			{
				Set<UrlPattern> urls = mappings.get(p);
				mappings.remove(urls);
			}
		}
	}

	@Override
	public void saveMappings(Site site) {
		Set<Permission> permissions = site.getPermissions();
		Iterator<Permission> it = permissions.iterator();
		while(it.hasNext())
		{
			Permission p = it.next();
			if(!mappings.containsKey(p))
			{
				Set<UrlPattern> urls = new HashSet<UrlPattern>();
				urls.addAll(p.getUrls());
				mappings.put(p, urls);
			}
		}
	}

	public void setTargetDAO(TargetDAO dao)
	{
		targetDao = dao;
	}
}
