package fr.jetoile.ipod.backuper;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.ID3Tag;

public class Mp3MetaDataUtils {
    public Mp3MetaDataUtils() {
    }

    public static String getAlbumMetaData(MusicMetadataSet src_set) {
        String album;
        ID3Tag id3Raw = src_set.id3v2Raw;

        if (id3Raw == null) {
            id3Raw = src_set.id3v1Raw;
        }
        IMusicMetadata metadata = id3Raw.values;

        album = metadata.getAlbum().trim();

        // si la valeur est vide ou null, on tente avec id3tag_v2
        id3Raw = src_set.id3v2Raw;
        if (id3Raw != null) {
            album = id3Raw.values.getAlbum().trim();
        }
        normalizeString(new String[]{album});
        return album;

    }

    public static String getSongMetaData(MusicMetadataSet src_set) {
        String song;
        ID3Tag id3Raw = src_set.id3v2Raw;

        if (id3Raw == null) {
            id3Raw = src_set.id3v1Raw;
        }
        IMusicMetadata metadata = id3Raw.values;

        song = metadata.getSongTitle().trim();

        // si la valeur est vide ou null, on tente avec id3tag_v2
        id3Raw = src_set.id3v2Raw;
        if (song.length() == 0) {
            song = id3Raw.values.getSongTitle().trim();
        }
        normalizeString(new String[]{song});
        return song;

    }

    public static String getTrackNumberMetaData(MusicMetadataSet src_set) {
        String trackNumber;
        ID3Tag id3Raw = src_set.id3v2Raw;

        if (id3Raw == null) {
            id3Raw = src_set.id3v1Raw;
        }
        IMusicMetadata metadata = id3Raw.values;

        trackNumber = metadata.getTrackNumberNumeric().toString().trim();

        // si la valeur est vide ou null, on tente avec id3tag_v2
        id3Raw = src_set.id3v2Raw;
        if (trackNumber.length() == 0) {
            trackNumber = id3Raw.values.getTrackNumberNumeric().toString().trim();
        }
        return normalizeTrackNumber(trackNumber);
    }

    public static String getArtistMetaData(MusicMetadataSet src_set) {
        String artist;
        ID3Tag id3Raw = src_set.id3v2Raw;

        if (id3Raw == null) {
            id3Raw = src_set.id3v1Raw;
        }
        IMusicMetadata metadata = id3Raw.values;

        artist = metadata.getArtist().trim();

        // si la valeur est vide ou null, on tente avec id3tag_v2
        id3Raw = src_set.id3v2Raw;
        if (id3Raw != null) {
            if (artist.length() == 0) {
                artist = id3Raw.values.getArtist().trim();
            }
        }
        normalizeString(new String[]{artist});
        return artist;
    }

    private static String normalizeTrackNumber(String trackNumber) {
        if (trackNumber.length() == 1) {
            trackNumber = "0" + trackNumber;
        }
        return trackNumber;
    }

    private static void normalizeString(String[] value) {
        if (value[0].indexOf("?") != -1) {
            value[0] = value[0].replaceAll("\\?", " ");
        }
        if (value[0].indexOf(".") != -1) {
            value[0] = value[0].replaceAll("\\.", " ");
        }
        if (value[0].indexOf("&") != -1) {
            value[0] = value[0].replaceAll("\\&", " ");
        }
        if (value[0].indexOf("'") != -1) {
            value[0] = value[0].replaceAll("\\'", " ");
        }
        if (value[0].indexOf("/") != -1) {
            value[0] = value[0].replaceAll("\\/", "-");
        }
        if (value[0].indexOf("\\") != -1) {
            value[0] = value[0].replaceAll("\\\\", "-");
        }
        if (value[0].indexOf("(") != -1) {
            value[0] = value[0].replaceAll("\\(", "");
        }
        if (value[0].indexOf(")") != -1) {
            value[0] = value[0].replaceAll("\\)", "");
        }
        if (value[0].indexOf(":") != -1) {
            value[0] = value[0].replaceAll("\\:", " ");
        }
    }
}