package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setLikes(0);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;
        
        //search for the artist
        for(Artist artist : artists){
            if(artist.getName() == artistName){
                artist1 = artist; //if found assign and
                break; //break the loop
            }
        }
        
        // If the artist is not found, create a new one
        if (artist1 == null) {
            artist1 = createArtist(artistName);
        }
        
        // Create and initialize the new album
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());
        
        // Add the new album to the albums list
        albums.add(album);
        
        // Get the current list of albums for the artist or create a new list if none exists
        List<Album> albumList = artistAlbumMap.getOrDefault(artist1, new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(artist1, albumList);
        
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album = null;
        
        //search for the song
        for(Album album1 : albums){
            if(album1.getTitle() == albumName){
                album = album1;
                break;
            }
        }
        if(album == null){
            throw new Exception("Album does not exist");
        }
        
        // Create a new song and set its properties
        Song song = new Song();
        song.setTitle(title);
        song.setLength(length);
        song.setLikes(0);
        
        // Add the song to the songs list
        songs.add(song);
        
        // Add the song to the albumSongMap
        if (albumSongMap.containsKey(album)) {
            List<Song> songList = albumSongMap.get(album);
            songList.add(song);
        }
        else {
            List<Song> songList = new ArrayList<>();
            songList.add(song);
            albumSongMap.put(album, songList);
        }
       
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User user1 : users){
            if(user1.getMobile() == mobile){
                user = user1;
                break;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlists.add(playlist);

        List<Song> songsWithLength = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength() == length){
                songsWithLength.add(song);
            }
        }        
        playlistSongMap.put(playlist, songsWithLength);

        List<User> initialListeners = new ArrayList<>();
        initialListeners.add(user);
        playlistListenerMap.put(playlist, initialListeners);
        
        creatorPlaylistMap.put(user, playlist);
        if(userPlaylistMap.containsKey(user)){
            List<Playlist> userPlayList = userPlaylistMap.get(user);
            userPlayList.add(playlist);
            userPlaylistMap.put(user,userPlayList);
        }
        else{
            List<Playlist> plays = new ArrayList<>();
            plays.add(playlist);
            userPlaylistMap.put(user,plays);
        }

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User user1 : users){
            if(user1.getMobile() == mobile){
                user = user1;
                break;
            }
        }
        if(user == null){
            throw new Exception("User does not exist");
        }  
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlists.add(playlist);
        
        List<Song> songsWithName = new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                songsWithName.add(song);
            }
        }
        playlistSongMap.put(playlist,songsWithName);

        List<User> list = new ArrayList<>();
        list.add(user);
        playlistListenerMap.put(playlist,list);

        creatorPlaylistMap.put(user,playlist);
        if(userPlaylistMap.containsKey(user)){
            List<Playlist> userPlayList = userPlaylistMap.get(user);
            userPlayList.add(playlist);
            userPlaylistMap.put(user,userPlayList);
        }
        else{
            List<Playlist> plays = new ArrayList<>();
            plays.add(playlist);
            userPlaylistMap.put(user,plays);
        }

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle()==playlistTitle){
                playlist=playlist1;
                break;
            }
        }
        if(playlist==null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User user1:listener){
            if(user1==user)
                return playlist;
        }

        listener.add(user);
        playlistListenerMap.put(playlist,listener);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        // Find the user by mobile number
        User user = getUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }

        // Find the song by title
        Song song = getSongByTitle(songTitle);
        if (song == null) {
            throw new Exception("Song does not exist");
        }

        // Check if the user has already liked the song
        List<User> likedUsers = songLikeMap.getOrDefault(song, new ArrayList<>());
        if (likedUsers.contains(user)) {
            return song; // User has already liked the song, do nothing
        }
        
        // Increment the song likes and add the user to the liked users list
        int likes = song.getLikes() + 1;
        song.setLikes(likes);
        likedUsers.add(user);
        songLikeMap.put(song, likedUsers);

        // Auto-like the corresponding artist
        Album album = getAlbumOfSong(song);
        if (album != null) {
            Artist artist = getArtistOfAlbum(album);
            if (artist != null) {
                int artistLikes = artist.getLikes() + 1;
                artist.setLikes(artistLikes);
            }
        }
    
        return song;
        
    }
    
    // Helper method to find a user by mobile number
    private User getUserByMobile(String mobile) {
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        return null;
    }
    // Helper method to find a song by title
    private Song getSongByTitle(String title) {
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                return song;
            }
        }
        return null;
    }
    
    // Helper method to get the album of a song
    private Album getAlbumOfSong(Song song) {
        for (Map.Entry<Album, List<Song>> entry : albumSongMap.entrySet()) {
            if (entry.getValue().contains(song)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    // Helper method to get the artist of an album
    private Artist getArtistOfAlbum(Album album) {
        for (Map.Entry<Artist, List<Album>> entry : artistAlbumMap.entrySet()) {
            if (entry.getValue().contains(album)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String mostPopularArtist() {
        Artist mostPopularArtist = null;
        int maxLikes = 0;
        for (Artist artist : artists) {
            int likes = artist.getLikes();
            if (likes >= maxLikes) {
                maxLikes = likes;
                mostPopularArtist = artist;
            }
        }
        return (mostPopularArtist != null) ? mostPopularArtist.getName() : null;
    }

    public String mostPopularSong() {
        int maxLikes = 0;
        Song mostPopularSong = null;

        for(Song song : songLikeMap.keySet()) {
            if(song.getLikes() >= maxLikes){
                mostPopularSong = song;
                maxLikes = song.getLikes();
            }
        }
        return mostPopularSong != null ? mostPopularSong.getTitle() : null;
    }
}
