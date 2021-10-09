package uz.mobilestudio.photo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import uz.mobilestudio.photo.R
import uz.mobilestudio.photo.databinding.FragmentRandomBinding
import uz.mobilestudio.photo.view_models.PhotosViewModel

class RandomFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    lateinit var binding: FragmentRandomBinding
    lateinit var viewModel: PhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomBinding.inflate(layoutInflater)

        binding.share.visibility = View.GONE
        binding.download.visibility = View.GONE
        binding.setBackground.visibility = View.GONE
        binding.effect.visibility = View.GONE
        binding.like.visibility = View.GONE

        loadPhoto()

        return binding.root
    }

    private fun loadPhoto() {
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        viewModel.getRandomPhoto().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.progress.visibility = View.GONE
                binding.share.visibility = View.VISIBLE
                binding.download.visibility = View.VISIBLE
                binding.setBackground.visibility = View.VISIBLE
                binding.effect.visibility = View.VISIBLE
                binding.like.visibility = View.VISIBLE
                Glide.with(this).load(it.urls.regular).into(binding.image)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.random)
    }
}