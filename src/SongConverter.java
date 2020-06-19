

public class SongConverter
{
	public static String convertSong(String song)
	{
		String toRemove = "\"_BPMChanges\":[],";
		int removeIndex = song.indexOf(toRemove);
		String result = song.substring(0, removeIndex) + song.substring(removeIndex+toRemove.length());
		//System.out.println(result);
		return result;
	}
}