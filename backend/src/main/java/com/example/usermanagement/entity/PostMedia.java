@Entity
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;   // Link từ Cloudinary hoặc AWS S3
    private String fileType;  // "IMAGE" hoặc "VIDEO"
    private String quality;   // "4K", "HD", "SD" (Để phục vụ việc hiển thị)
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}

