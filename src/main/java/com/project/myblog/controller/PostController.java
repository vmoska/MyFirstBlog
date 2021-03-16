package com.project.myblog.controller;

import com.project.myblog.entity.*;
import com.project.myblog.payload.request.PostCountriesRequest;
import com.project.myblog.payload.response.*;
import com.project.myblog.repository.*;
import com.project.myblog.service.PostService;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@Validated
public class PostController {

	@Autowired
	PostRepository postRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	LanguageRepository languageRepository;
	@Autowired
	UserService userService;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	MainLandRepository mainLandRepository;
	@Autowired
	TagRepository tagRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	PostVisitedRepository postVisitedRepository;
	@Autowired
	AuthorityRepository authorityRepository;
	@Autowired
	PostService postService;

	@Transactional
	@PostMapping("private/add-post")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> addPost(/* @RequestBody @Valid PostRequest postRequest */
			@RequestParam MultipartFile file, @RequestParam @NotEmpty @NotBlank @Size(min = 1, max = 100) String title,
			@RequestParam @NotEmpty @NotBlank @Size(min = 1, max = 255) String overview,
			@RequestParam @NotEmpty @NotBlank @Size(min = 1, max = 64000) String content,
			@RequestParam @NotEmpty @NotBlank @Size(min = 2, max = 2) String langCode,
			HttpServletRequest request) {

		Object msg = null;
		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;
		response = new ResponseEntityHandler(msg, request, status);

		boolean titleExists = postRepository.existsByTitle(title);
		if (titleExists) {
			response.setMsg("Title already present");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Optional<Language> l = languageRepository.findById(langCode);

		if (l.isEmpty()) {
			response.setMsg("Language not found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		User u = userService.getAuthenticatedUser();
		Post p = new Post(title, overview, content, u, l.get());


		postRepository.save(p);
		response.setMsg("New Post added");

		return response.getResponseEntity();
	}


	@PutMapping("private/update-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> updatePost(
			@PathVariable @NotNull long id,
			@RequestParam MultipartFile file, @NotEmpty @NotBlank @Size(min = 1, max = 100) String title,
			@NotEmpty @NotBlank @Size(min = 1, max = 255) String overview,
			@NotEmpty @NotBlank @Size(min = 1, max = 64000) String content,
			@NotEmpty @NotBlank @Size(min = 2, max = 2) String langCode,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);
		String msg = "";

		User u = userService.getAuthenticatedUser();

		Optional<Post> post = postRepository.findById(id);

		if (post.isEmpty()) {
			response = new ResponseEntityHandler("Post not found", request, HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		Optional<Language> l = languageRepository.findById(langCode);

		if (l.isEmpty()) {
			response = new ResponseEntityHandler("Language not found", request, HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (u.getId() != post.get().getAuthor().getId()) {
			response = new ResponseEntityHandler("You cannot update this post", request, HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Optional<Post> duplicateTitle = postRepository.findByTitle(title);

		if (duplicateTitle.isPresent()) {
			if (duplicateTitle.get().getId() != id) {
				response = new ResponseEntityHandler("There is already another post with the same title", request,
						HttpStatus.FORBIDDEN);
				return response.getResponseEntity();
			}
		}


		post.get().setTitle(title);
		post.get().setOverview(overview);
		post.get().setContent(content);
		post.get().setLanguage(l.get());

		post.get().setApproved(false);
		post.get().setPublished(false);

		postRepository.save(post.get());

		response.setMsg("Post updated.");
		response.setMsg(msg);
		return response.getResponseEntity();

	}

	@PutMapping("private/publish-unpublish-post/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> publishUnpublishPost(
			@PathVariable @NotNull long id,
			@RequestParam @NotNull boolean x, HttpServletRequest request) {

		Object msg = null;
		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;

		Optional<Post> post = postRepository.findById(id);

		if (post.isEmpty()) {
			response = new ResponseEntityHandler("Post not found", request, HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (x) {
			if (!post.get().getApproved()) {
				response = new ResponseEntityHandler("Approve the post before publish it", request,
						HttpStatus.NOT_FOUND);
				return response.getResponseEntity();
			}
			post.get().setPublished(true);
			msg = "Post " + id + " has been published";
		} else {
			post.get().setPublished(false);
			msg = "Post " + id + " has been unpublished";
		}

		postRepository.save(post.get());

		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();

	}

	@PutMapping("private/approve-post/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> approvePost(
			@PathVariable @NotNull long id,
			@RequestParam @NotNull boolean x, HttpServletRequest request) {

		Object msg = null;
		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;

		Optional<Post> post = postRepository.findById(id);
		if (post.isEmpty()) {
			response = new ResponseEntityHandler("Post not found", request, HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (!x) {
			post.get().setApproved(false);
			post.get().setPublished(false);
			msg = "Post " + id + " has been disapproved and unpublished";
		} else {
			post.get().setApproved(true);
			msg = "Post " + id + " has been approved";
		}

		postRepository.save(post.get());

		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();

	}

	@PutMapping("private/add-countries-to-post")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> addCountriesToPost(
			@RequestBody @Valid PostCountriesRequest postCountriesRequest,
			HttpServletRequest request) {

		String msg = "Countries added to post " + postCountriesRequest.getId();
		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;

		Optional<Post> post = postRepository.getPostWithCountries(postCountriesRequest.getId());
		if (post.isEmpty()) {
			response = new ResponseEntityHandler("Post not found", request, HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (!post.get().getAuthor().getId().equals(userService.getAuthenticatedUser().getId())) {
			msg = "You are not the owner of this post: " + postCountriesRequest.getId();
			status = HttpStatus.FORBIDDEN;
			response = new ResponseEntityHandler(msg, request, status);
			return response.getResponseEntity();
		}

		Set<Country> cs = countryRepository.findByCountryCodeIn(postCountriesRequest.getCountriesCode());

		// cs.forEach(post.get().getCountries()::add);
		post.get().getCountries().addAll(cs);

		postRepository.save(post.get());

		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-by-country")
	public ResponseEntity<ApiResponseCustom> getPostByCountry(
			@RequestParam @NotBlank @NotEmpty String countryCode,
			HttpServletRequest request) {

		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;

		List<Post> ps = postRepository.getPostCountriesResponse(countryCode);

		List<PostCountriesResponse> pcrs = ps.stream()
				.map(p -> new PostCountriesResponse(p.getId(), p.getTitle(), p.getOverview(), p.getUpdatedAt(),
						p.getAuthor().getUsername(), p.getLanguage().getLangName(), (Set<String>) p.getCountries()
								.stream().map(Country::getCountryCode).collect(Collectors.toSet())))
				.collect(Collectors.toList());

		response = new ResponseEntityHandler(pcrs, request, status);
		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-by-mainland")
	public ResponseEntity<ApiResponseCustom> getPostByMainLand(
			@RequestParam @NotBlank @NotEmpty String mainLandName,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);
		Optional<MainLand> mainLandOptional = mainLandRepository.findById(mainLandName);

		if (mainLandOptional.isEmpty()) {
			response.setMsg("This mainland doesn't exist in the database");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		List<Post> postList = postRepository.getPostMainLandResponse(mainLandName);

		for (int i = 0; i < postList.size(); i++) {
			Post postTmp = postList.get(i);
			postList.remove(i);

			if (!postList.contains(postTmp))
				postList.add(postTmp);
		}

		List<PostMainLandResponse> postMainLandList = postList.stream()
				.map(p -> new PostMainLandResponse(p.getId(), p.getTitle(), p.getOverview(), p.getUpdatedAt(),
						p.getAuthor().getUsername(), p.getLanguage().getLangName(), p.getCountries().stream()
								.map(c -> c.getMainLand().getMainLandName()).collect(Collectors.toSet())))
				.collect(Collectors.toList());

		response.setMsg(postMainLandList);

		return response.getResponseEntity();
	}

	@PostMapping("private/add-tag-to-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> addTagToPost(@PathVariable @NotNull long id,
			@RequestParam @NotBlank @NotEmpty String tagName, HttpServletRequest request) {

		Object msg = " ";
		HttpStatus status = HttpStatus.OK;
		ResponseEntityHandler response;
		response = new ResponseEntityHandler(msg, request, status);

		Optional<Post> p = postRepository.findById(id);
		if (p.isEmpty()) {
			response.setMsg("Post is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		User u = userService.getAuthenticatedUser();
		if (!u.getId().equals(p.get().getAuthor().getId())) {
			response.setMsg("You cannot update this post");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Optional<Tag> t = tagRepository.findById(tagName.toUpperCase());
		if (t.isEmpty()) {
			response.setMsg("Tag is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (p.get().getTags().contains(t.get())) {
			response.setMsg("Tag already assigned to post");
			response.setStatus(HttpStatus.BAD_REQUEST);
			return response.getResponseEntity();
		}

		p.get().getTags().add(t.get());
		postRepository.save(p.get());
		response.setMsg("Tag added to post");
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();

	}

	@PutMapping("private/remove-tag-from-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> removeTagFromPost(@PathVariable @NotNull long id,
			@RequestParam @NotBlank @NotEmpty String tagName, HttpServletRequest request) {
		ResponseEntityHandler response = new ResponseEntityHandler(request);

		Optional<Post> p = postRepository.findById(id);
		if (p.isEmpty()) {
			response.setMsg("Post is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		User u = userService.getAuthenticatedUser();
		if (!u.getId().equals(p.get().getAuthor().getId())) {
			response.setMsg("You cannot update this post");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Optional<Tag> t = tagRepository.findById(tagName.toUpperCase());
		if (t.isEmpty()) {
			response.setMsg("Tag does not exist");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		if (!p.get().getTags().contains(t.get())) {
			response.setMsg("The post does not have that tag");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		p.get().getTags().remove(t.get());
		postRepository.save(p.get());
		response.setMsg("Tag removed");
		response.setStatus(HttpStatus.OK);

		return response.getResponseEntity();

	}

	@GetMapping("public/find-posts-by-tag")
	public ResponseEntity<ApiResponseCustom> findPostsByTag(
			@RequestParam @NotBlank @NotEmpty String tagName,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);

		Optional<Tag> t = tagRepository.findById(tagName.toUpperCase());

		if (t.isEmpty()) {
			response.setMsg("Tag does not exist");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		List<PostTagResponse> ps = postRepository.getPostsByTag(tagName);

		if (ps.isEmpty()) {
			response.setMsg("No posts found with tag: " + tagName);
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		response.setMsg(ps);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();

	}

	@GetMapping("public/get-post-detail")
	public ResponseEntity<ApiResponseCustom> getPostDetail(@RequestParam @NotNull long postId,
			HttpServletRequest request, @RequestHeader(value = "User-Agent") String userAgent) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);

		String ip = request.getRemoteAddr();

		Optional<Post> p = postRepository.findByIdAndPublishedTrue(postId);
		if (p.isEmpty()) {
			response.setMsg("Post is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		User user = userService.getAuthenticatedUser();
		long userId = 0L;

		if (user != null) {
			if (user.getAuthorities().contains(new Authority(AuthorityName.ROLE_READER))) {

				userId = user.getId();
			}
		}
		PostVisited pv = new PostVisited(p.get(), userId, ip, userAgent);
		postVisitedRepository.save(pv);

		PostDetailResponse pdr = postRepository.getPostDetail(postId);
		List<CommentResponse> cr = commentRepository.getCommentResponse(postId);
		pdr.setComments(cr);

		response.setMsg(pdr);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-by-author")
	public ResponseEntity<ApiResponseCustom> getPostByAuthor(
			@RequestParam @NotBlank @NotEmpty String authorName,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);
		List<PostResponse> postList = postRepository.getPostsByAuthor(authorName);

		if (postList.isEmpty()) {
			response.setMsg("There aren't posts from this author");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		response.setMsg(postList);

		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-count-comments")
	public ResponseEntity<ApiResponseCustom> getPostByAuthor(HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);
		List<PostCountCommentsResponse> postList = postRepository.getPostCountCommentResponse();

		if (postList.isEmpty()) {
			response.setMsg("There aren't posts from this author");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		response.setMsg(postList);

		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-count-tags")
	public ResponseEntity<ApiResponseCustom> getPostCountTags(HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);
		List<PostCountTagResponse> postList = postRepository.getPostCountTagsResponse();

		if (postList.isEmpty()) {
			response.setMsg("There aren't posts from this author");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		response.setMsg(postList);

		return response.getResponseEntity();
	}

	@GetMapping("public/get-posts-by-avg")
	public ResponseEntity<ApiResponseCustom> getPosts(
			@RequestParam(defaultValue = "ASC") String ordered,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);

		List<PostResponse> postList = postRepository.getPostsByAvg(ordered);

		if (postList.isEmpty()) {
			response.setMsg("No post found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		response.setMsg(postList);
		return response.getResponseEntity();

	}


	

}
