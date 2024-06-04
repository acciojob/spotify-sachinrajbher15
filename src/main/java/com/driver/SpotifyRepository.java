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
        user.setName(name) = name;
        user.setMobile(mobille) = mobile;
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name) = name;
        artist.setLikes(0);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;
        for(Artist a: artists){
            if(a.getName() == artistName){
                artist1 = a;
                break;
            }
        }
        if(artist1 == null){
            artist1 = createArtist(artistName);
            Album album = new Album();
            album.setTitle(title);
            album.setReleaseDate(new Date());
            albums.add(album);
            List<Album> list = new ArrayList<>();
            list.add(album);
            artistAlbumMap.put(artist1, list);
            return album;
        }
        else{
            Album album = new Album();
            Album album = new Album();
            album.setTitle(title);
            album.setReleaseDate(new Date());
            albums.add(album);
            List<Album> list = new ArrayList<>();
            list.add(album);
            artistAlbumMap.put(artist1, list);
            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album = null;
        for(Album a : albums){
            if(a.getTitle() == albumName){
                album = a;
                break;
            }
        }
        if(album==null)
            throw new Exception("Album does not exist");
        else {
            Song song = new Song();
            song.setTitle(title);
            song.setLength(length);
            song.setLikes(0);
            songs.add(song);

            if(albumSongMap.containsKey(album)){
                List<Song> l = albumSongMap.get(album);
                l.add(song);
                albumSongMap.put(album,l);
            }else{
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                albumSongMap.put(album,songList);
            }
            return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User u : users){
            if(u.getMobile() == mobile){
                user = u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song s : songs){
                if(s.getLength() == length){
                    list.add(s);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list2 = new ArrayList<>();
            list2.add(user);
            playlistListenerMap.put(playlist,list2);
            
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

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User u : users){
            if(u.getMobile()==mobile){
                user = u;
                break;
            }
        }
        if(user == null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song s : songs){
                if(songTitles.contains(s.getTitle())){
                    list.add(s);
                }
            }
            playlistSongMap.put(playlist,list);

            List<User> list2 = new ArrayList<>();
            list2.add(user);
            playlistListenerMap.put(playlist,list2);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }
            return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(User u : users){
            if(u.getMobile()==mobile){
                user = u;
                break;
            }
        }
        if(user == null)
            throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist p : playlists){
            if(p.getTitle() == playlistTitle){
                playlist=p;
                break;
            }
        }
        if(playlist==null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User u : listener){
            if(u == user)
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
        User user = null;
        for(User u : users){
            if(u.getMobile() == mobile){
                user = u;
                break;
            }
        }
        if(user == null)
            throw new Exception("User does not exist");

        Song song = null;
        for(Song s : songs){
            if(s.getTitle() == songTitle){
                song = s;
                break;
            }
        }
        if (song == null)
            throw new Exception("Song does not exist");

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }
            else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album a : albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(a);
                    if(songList.contains(song)){
                        album = a;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist a : artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(a);
                    if (albumList.contains(album)){
                        artist = a;
                        break;
                    }
                }
                int likes1 = artist.getLikes() + 1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }
        else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album a : albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(a);
                if(songList.contains(song)){
                    album = a;
                    break;
                }
            }
            Artist artist = null;
            for(Artist a : artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(a);
                if (albumList.contains(album)){
                    artist = a;
                    break;
                }
            }
            int likes1 = artist.getLikes() + 1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist1=null;

        for(Artist a : artists){
            if(a.getLikes() >= max){
                artist1 = a;
                max = a.getLikes();
            }
        }
        if(artist1==null)
            return null;
        else
            return artist1.getName();
    }

    public String mostPopularSong() {
        int max=0;
        Song song = null;

        for(Song s : songLikeMap.keySet()){
            if(s.getLikes() >= max){
                song = s;
                max = s.getLikes();
            }
        }
        if(song==null)
            return null;
        else
            return song.getTitle();
    }
}
