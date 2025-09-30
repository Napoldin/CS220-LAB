package cs220.overarching;

public class Player {

    private int rank = 0;
    final private String name;
    final private int elo;
    final private double gxe;
    final private int glickoRating;
    final private int glickoDeviation;
    private String coil = "--";

    public Player(int rank, String name, int elo, double gxe, int glickoRating, int glickoDeviation, String coil) {
        this.rank = rank;
        this.name = name;
        this.elo = elo;
        this.gxe = gxe;
        this.glickoRating = glickoRating;
        this.glickoDeviation = glickoDeviation;
        this.coil = coil;
    }

    public Player(String name, int elo, double gxe, int glickoRating, int glickoDeviation) {
        this.name = name;
        this.elo = elo;
        this.gxe = gxe;
        this.glickoRating = glickoRating;
        this.glickoDeviation = glickoDeviation;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public int getElo() { return elo; }
    public double getGxe() { return gxe; }
    public int getGlickoRating() { return glickoRating; }
    public int getGlickoDeviation() { return glickoDeviation; }
    public String getCoil() { return coil; }


    public String toString() {
        if (this.rank > 0){
            return String.format("rank: %d name: %s, elo: %d, gxe: %.2f%%, glicko: %d±%d, coil: %s",
                    rank, name, elo, gxe, glickoRating, glickoDeviation, coil);
        } else {
            return String.format("name: %s, elo: %d, gxe: %.2f%%, glicko: %d±%d",
                    name, elo, gxe, glickoRating, glickoDeviation);
        }
    }
}
