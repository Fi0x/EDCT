package com.fi0x.edct.gui.visual;

import com.sun.glass.ui.Window;
import javafx.stage.Stage;
import org.bridj.Pointer;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ProgressBar
{
    private final ExecutorService executor;
    private final AtomicReference<ITaskbarList3> iTaskbarList3;
    private final GlassApiHWNDStrategy hwndStrategy;
    private final AtomicReference<Stage> stage;

    public ProgressBar()
    {
        executor = Executors.newSingleThreadExecutor(ProgressBar.DaemonThread::new);
        iTaskbarList3 = new AtomicReference<>();
        hwndStrategy = new GlassApiHWNDStrategy();
        stage = new AtomicReference<>();

        instantiateITaskbarList();
    }

    public synchronized void setStage(Stage stage)
    {
        this.stage.set(stage);
    }

    private void instantiateITaskbarList()
    {
        executor.execute(() ->
        {
            try
            {
                iTaskbarList3.set(COMRuntime.newInstance(ITaskbarList3.class));
            } catch(ClassNotFoundException | RuntimeException ignored)
            {
            }
        });
    }

    private void setProgressState(Stage stage, Type type)
    {
        if(stage != null)
        {
            final Pointer<Integer> pointer = getPointer(stage);
            executor.execute(() -> iTaskbarList3.get().SetProgressState(pointer, type.getBridjPair()));
        }
    }

    public void showCustomProgress(double startValue, Type type)
    {
        if(validate(stage.get()))
        {
            Pointer<Integer> pointer = getPointer(stage.get());
            executor.execute(() ->
            {
                iTaskbarList3.get().SetProgressValue(pointer, (long) (startValue * 100), 100);
                iTaskbarList3.get().SetProgressState(pointer, type.getBridjPair());
            });
        }
    }

    public void setProgressType(Type type)
    {
        if(!validate(stage.get())) return;
        setProgressState(stage.get(), type);
    }

    private boolean validate(Stage stage)
    {
        if(!OS.isWindows7OrLater()) return false;
        if(stage == null) return false;
        return stage.isShowing();
    }

    private Pointer<Integer> getPointer(Stage stage)
    {
        long windowHandle = hwndStrategy.getHWND(stage);
        return (Pointer<Integer>) Pointer.pointerToAddress(windowHandle);
    }

    private static final class DaemonThread extends Thread
    {
        DaemonThread(Runnable runnable)
        {
            super(runnable);
            setDaemon(true);
        }
    }

    public enum Type
    {
        ERROR(ITaskbarList3.TbpFlag.TBPF_ERROR),
        NO_PROGRESS(ITaskbarList3.TbpFlag.TBPF_NOPROGRESS),
        NORMAL(ITaskbarList3.TbpFlag.TBPF_NORMAL);

        private final ITaskbarList3.TbpFlag bridjPair;

        Type(ITaskbarList3.TbpFlag bridjPair)
        {
            this.bridjPair = bridjPair;
        }

        public ITaskbarList3.TbpFlag getBridjPair()
        {
            return this.bridjPair;
        }
    }

    private static class GlassApiHWNDStrategy
    {
        public long getHWND(Stage stage)
        {
            return Window.getWindows().get(indexOf(stage)).getNativeWindow();
        }

        public int indexOf(Stage stage)
        {
            return javafx.stage.Window.getWindows().indexOf(stage);
        }
    }

    private static class OS
    {
        private static final String OS = System.getProperty("os.name");
        private static final String VERSION = System.getProperty("os.version");
        private static final boolean WINDOWS;
        private static final boolean WINDOWS_7_OR_LATER;

        public static boolean isWindows7OrLater()
        {
            return WINDOWS_7_OR_LATER;
        }

        public static boolean versionGreaterThanOrEqualTo(float value)
        {
            try
            {
                return Float.parseFloat(VERSION) >= value;
            } catch(Exception var2)
            {
                return false;
            }
        }

        static
        {
            WINDOWS = OS.startsWith("Windows");
            WINDOWS_7_OR_LATER = WINDOWS && versionGreaterThanOrEqualTo(6.1F);
        }
    }

}