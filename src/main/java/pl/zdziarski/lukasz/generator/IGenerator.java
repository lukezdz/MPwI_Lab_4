package pl.zdziarski.lukasz.generator;

public interface IGenerator<T>
{
	public IGenerator generate();
	public IGenerator print();
	public IGenerator log();
	public IGenerator presentLogs(PresentationMode mode);
	public void clearLogs();
	public T getGenerated();


	public enum	PresentationMode {
		RAW,
		MATRIX
	}
}
