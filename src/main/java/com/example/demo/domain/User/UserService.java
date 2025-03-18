package com.example.demo.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.Post.PostRepository;
import com.example.demo.generated.Post;
import com.example.demo.generated.User;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * ID からユーザー DTO を取得する
   * 
   * @param userId
   * @return 合致するユーザー DTO (存在しない場合は null)
   */
  public Optional<UserDto> findById(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * セッションユーザー DTO を取得する
   * 
   * @return セッションユーザー DTO (存在しない場合は null)
   */
  public Optional<UserDto> getSessionUser() {
    Optional<User> user = userRepository.getSessionUser();
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * ユーザー ID に紐づく投稿を取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<PostDto> getUserPosts(String userId) {
    Stream<Post> posts = postRepository.findByUserId(userId).stream();
    return posts.map(p -> new PostDto(p.getId(), p.getUserId(), p.getContent(), p.getCreatedAt())).toList();
  }

  /**
   * ユーザーを追加する
   * 追加に成功した場合、追加したユーザー DTO を返す
   * 
   * @param user
   * @return 追加したユーザー DTO (追加に失敗した場合は null)
   */
  public Optional<UserDto> add(User user) {
    try {
      // バリデーション
      // id が重複している場合は失敗
      if (userRepository.findById(user.getId()).isPresent()) {
        throw new Exception("ID is already used.");
      }
      // id が半角英数字+ハイフン+アンダースコアでない場合は失敗
      if (!user.getId().matches("^[a-zA-Z0-9_-]+$")) {
        throw new Exception("ID is invalid.");
      }
      // ユーザー名が空白の場合は失敗
      if (user.getUsername().isBlank()) {
        throw new Exception("Username is empty.");
      }
      // email が重複している場合は失敗
      if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new Exception("Email is already used.");
      }
      // email が正規表現に合致しない場合は失敗
      EmailValidator emailValidator = new EmailValidator();
      if (!emailValidator.isValid(user.getEmail(), null)) {
        throw new Exception("Email is invalid.");
      }
      // パスワードが 6文字以上でない場合は失敗
      if (user.getPassword().length() < 6) {
        throw new Exception("Password is too short.");
      }
      // パスワードが半角英数字+記号でない場合は失敗
      if (!user.getPassword().matches("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$")) {
        throw new Exception("Password is invalid.");
      }

      // パスワードをハッシュ化して保存する
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userRepository.insert(user);
    } catch (Exception e) {
      return Optional.empty();
    }
    return Optional.of(new UserDto(user.getId(), user.getUsername(), user.getProfile(), user.getEmail()));
  }
}
